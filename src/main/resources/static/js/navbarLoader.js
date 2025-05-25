function loadNavbar() {
    fetch("/navbar.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("navbar-placeholder").innerHTML = html;
            const script = document.createElement("script");
            script.src = "/js/navbar.js";
            script.onload = () => window.setupNavbar();
            document.body.appendChild(script);
        });
}
