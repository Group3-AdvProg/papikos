document.getElementById("filter-form").addEventListener("submit", async function (e) {
  e.preventDefault();

  const userId = document.getElementById("userId").value;
  const type = document.getElementById("type").value;
  const from = document.getElementById("from").value;
  const to = document.getElementById("to").value;
  const resultsDiv = document.getElementById("results");

  let url = `/api/transaction/user/${userId}`;

  if (type && from && to) {
    url = `/api/transaction/filter?userId=${userId}&type=${type}&from=${from}&to=${to}`;
  } else if (type) {
    url = `/api/transaction/type?userId=${userId}&type=${type}`;
  } else if (from && to) {
    url = `/api/transaction/date?userId=${userId}&from=${from}&to=${to}`;
  }

  const res = await fetch(url);
  const transactions = await res.json();

  if (transactions.length === 0) {
    resultsDiv.innerHTML = `<p class="text-gray-500">No transactions found.</p>`;
    return;
  }

  resultsDiv.innerHTML = transactions.map(tx => `
    <div class="border p-3 rounded bg-gray-50">
      <strong>${tx.type}</strong> - ${tx.amount.toLocaleString('id-ID')} via ${tx.method} <br/>
      <span class="text-xs text-gray-500">${tx.timestamp}</span>
    </div>
  `).join("");
});
