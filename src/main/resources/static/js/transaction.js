let currentUserId = null;

document.addEventListener("DOMContentLoaded", async () => {
  const token = sessionStorage.getItem("token");

  if (!token) {
    alert("Please login to view transaction history.");
    return;
  }

  try {
    const res = await fetch("/api/auth/users/me", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!res.ok) throw new Error("Failed to get user info");

    const user = await res.json();
    currentUserId = user.id;

    // Auto-fill the User ID field and disable it
    const userIdInput = document.getElementById("userId");
    userIdInput.value = currentUserId;
    userIdInput.disabled = true;

    // Load default transaction history
    fetchAndRenderTransactions(`/api/transaction/user/${currentUserId}`, token);
  } catch (err) {
    console.error("Auth check failed:", err);
    alert("Unable to load user info. Please log in again.");
    window.location.href = "/login.html";
  }
});

document.getElementById("filter-form").addEventListener("submit", async function (e) {
  e.preventDefault();

  const type = document.getElementById("type").value;
  const from = document.getElementById("from").value;
  const to = document.getElementById("to").value;
  const resultsDiv = document.getElementById("results");

  const token = sessionStorage.getItem("token");
  if (!token || !currentUserId) {
    alert("Missing authentication.");
    return;
  }

  let url = `/api/transaction/user/${currentUserId}`;

  if (type && from && to) {
    url = `/api/transaction/filter?userId=${currentUserId}&type=${type}&from=${from}&to=${to}`;
  } else if (type) {
    url = `/api/transaction/type?userId=${currentUserId}&type=${type}`;
  } else if (from && to) {
    url = `/api/transaction/date?userId=${currentUserId}&from=${from}&to=${to}`;
  }

  fetchAndRenderTransactions(url, token);
});

async function fetchAndRenderTransactions(url, token) {
  const resultsDiv = document.getElementById("results");

  try {
    const res = await fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // Log status and response text for debugging
    const text = await res.text();
    console.log("Status:", res.status, "Response:", text);

    // Send error to backend for terminal logging
    fetch("/api/log", {
      method: "POST",
      headers: { "Content-Type": "text/plain" },
      body: `Status: ${res.status}, Response: ${text}`
    });

    if (res.status === 401) {
      alert("Session expired. Please log in again.");
      window.location.href = "/login.html";
      return;
    }

    const transactions = JSON.parse(text);

    if (transactions.length === 0) {
      resultsDiv.innerHTML = `<p class="text-gray-500">No transactions found.</p>`;
      return;
    }

    resultsDiv.innerHTML = transactions.map(tx => `
      <div class="border p-3 rounded bg-gray-50">
        <strong>${tx.type}</strong> - Rp ${tx.amount.toLocaleString('id-ID')} via ${tx.method} <br/>
        <span class="text-xs text-gray-500">${new Date(tx.timestamp).toLocaleString("id-ID")}</span>
      </div>
    `).join("");
  } catch (err) {
    console.error("Error fetching transactions:", err);
    resultsDiv.innerHTML = `<p class="text-red-500">Failed to load transactions.</p>`;
  }
}
