// createProject.js
document.addEventListener('DOMContentLoaded', () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const form = document.getElementById('createProjectForm');
    const errorDiv = document.getElementById('errorMsg');
    const cancelBtn = document.getElementById('cancelBtn');

    cancelBtn.addEventListener('click', () => {
        window.location.href = '/projects.html';
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('name').value.trim();
        const description = document.getElementById('description').value.trim();
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';

        try {
            const resp = await fetch('http://localhost:8080/api/projects', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    name,
                    description,
                    owner: { id: Number(userId) }
                })
            });
            if (!resp.ok) {
                const errorObj = await resp.json();
                throw new Error(errorObj.message || 'Failed to create project.');
            }
            window.location.href = '/projects.html';
        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    });
});
