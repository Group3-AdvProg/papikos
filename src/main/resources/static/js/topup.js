document.getElementById("topup-form").addEventListener("submit", async function (e) {
  e.preventDefault();

  const userId = document.getElementById("userId").value;
  const amount = parseFloat(document.getElementById("amount").value);
  const method = document.getElementById("method").value;
  const msgEl = document.getElementById("response-msg");

  const response = await fetch("/api/wallet/topup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userId, amount, method }),
  });

  const data = await response.json();

  msgEl.textContent = data.message;
  msgEl.className = data.status === "SUCCESS"
    ? "text-green-600 mt-4 font-semibold"
    : "text-red-600 mt-4 font-semibold";

  if (data.redirectTo) {
    setTimeout(() => {
      window.location.href = data.redirectTo;
    }, 2000);
  }
});
