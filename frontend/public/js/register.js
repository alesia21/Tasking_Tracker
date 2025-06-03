document.addEventListener('DOMContentLoaded', () => {
    renderNavBar(); // Show Login/Register in header
    const form = document.getElementById('registerForm');
    const errorDiv = document.getElementById('errorMsg');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';

        try {
            const resp = await fetch('http://localhost:8080/api/users', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password })
            });

            if (!resp.ok) {
                const errorObj = await resp.json();
                throw new Error(errorObj.message || 'Registration failed.');
            }
            const newUser = await resp.json();
            // Automatically “log in” the new user
            localStorage.setItem('userId', newUser.id);
            window.location.href = '/projects.html';
        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    });
});
