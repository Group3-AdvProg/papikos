window.setupNavbar = function () {
    const token = sessionStorage.getItem("token");
    const navbar = document.getElementById("navbarUserArea");

    function showGuestNavbar() {
        navbar.innerHTML = `
            <li class="nav-item"><a class="btn btn-outline-primary me-2" href="/login.html">Login</a></li>
            <li class="nav-item"><a class="btn btn-primary" href="/register.html">Sign up</a></li>
        `;
    }

    if (!token) {
        showGuestNavbar();
        return;
    }

    fetch("/api/auth/me", {
        headers: { Authorization: "Bearer " + token }
    })
        .then(res => {
            if (!res.ok) throw new Error("Invalid token");
            return res.json();
        })
        .then(user => {
            const links = [];
            const role = user.role;

            if (role === "ROLE_ADMIN") {
                links.push(`<li class="nav-item"><a class="nav-link" href="/admin.html">Admin Dashboard</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/inbox.html">Inbox</a></li>`);
            } else if (role === "ROLE_LANDLORD") {
                links.push(`<li class="nav-item"><a class="nav-link" href="/management.html">My Houses</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/wallet.html">My Wallet</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/chat.html"><i class="bi bi-chat-dots"></i> Chat</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/inbox.html">Inbox</a></li>`);
            } else if (role === "ROLE_TENANT") {
                links.push(`<li class="nav-item"><a class="nav-link" href="/rental.html">Browse Rentals</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/wallet.html">My Wallet</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/chat.html"><i class="bi bi-chat-dots"></i> Chat</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/inbox.html">Inbox</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/wishlist.html">My Wishlist</a></li>`);
                links.push(`<li class="nav-item"><a class="nav-link" href="/myBookings.html">My Bookings</a></li>`);
            }

            links.push(`
                <li class="nav-item dropdown" id="user-dropdown">
                    <a class="btn btn-outline-primary dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                        <span id="user-email">${user.email}</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="#" id="logout-btn">Logout</a></li>
                    </ul>
                </li>
            `);

            navbar.innerHTML = links.join("");

            const dropdownTrigger = document.querySelector('[data-bs-toggle="dropdown"]');
            if (dropdownTrigger) new bootstrap.Dropdown(dropdownTrigger);

            document.getElementById("logout-btn").addEventListener("click", () => {
                sessionStorage.removeItem("token");
                window.location.href = "/";
            });
        })
        .catch(() => {
            sessionStorage.removeItem("token");
            showGuestNavbar();
        });
};
