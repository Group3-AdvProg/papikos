document.addEventListener("DOMContentLoaded", function () {
  const token = localStorage.getItem("token");

  try {
    const response = await fetch("/api/payment/pay-rent", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify({ tenantId, landlordId, amount }),
    });

    // Handle 401 directly
    if (response.status === 401) {
      msgEl.textContent = "Unauthorized: Please log in again.";
      msgEl.className = "text-red-600 mt-4 font-semibold";
      return;
    }

    // Handle empty body gracefully
    const text = await response.text();
    const data = text ? JSON.parse(text) : { status: "FAIL", message: "No response body from server." };

    msgEl.textContent = data.message;
    msgEl.className = data.status === "SUCCESS"
      ? "text-green-600 mt-4 font-semibold"
      : "text-red-600 mt-4 font-semibold";

    if (data.redirectTo) {
      setTimeout(() => {
        window.location.href = data.redirectTo;
      }, 2000);
    }

  } catch (err) {
    msgEl.textContent = "Something went wrong. Please try again.";
    msgEl.className = "text-red-600 mt-4 font-semibold";
    console.error("Fetch error:", err);
  }

  // Also keep your payrent form submission logic here
});
