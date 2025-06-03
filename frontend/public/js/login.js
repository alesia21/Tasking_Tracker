document.addEventListener('DOMContentLoaded', () => {
    renderNavBar(); // Show Login/Register in header (no userId)
    const form = document.getElementById('loginForm');
    const errorDiv = document.getElementById('errorMsg');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const userId = document.getElementById('userId').value.trim();
        const password = document.getElementById('password').value.trim();
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';

        if (!userId) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = 'Please enter your User ID.';
            return;
        }

        try {
            // Verify the user exists by ID
            const resp = await fetch(`http://localhost:8080/api/users/${userId}`);
            if (!resp.ok) {
                throw new Error('User not found. Please register first.');
            }
            // We’re not actually checking password server‐side.
            localStorage.setItem('userId', userId);
            window.location.href = '/projects.html';
        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    });
});