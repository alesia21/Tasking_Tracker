document.addEventListener('DOMContentLoaded', async () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const params = new URLSearchParams(window.location.search);
    const projectId = params.get('projectId');
    const errorDiv = document.getElementById('errorMsg');
    const form = document.getElementById('editProjectForm');
    const cancelBtn = document.getElementById('cancelBtn');

    if (!projectId) {
        window.location.href = '/projects.html';
        return;
    }

    cancelBtn.addEventListener('click', () => {
        window.location.href = '/projects.html';
    });

    // Pre‐fill form with existing project data
    try {
        const resp = await fetch(`http://localhost:8080/api/projects/${projectId}`);
        if (!resp.ok) throw new Error('Project not found.');
        const project = await resp.json();
        if (!project.owner || project.owner.id !== Number(userId)) {
            throw new Error('You do not have permission to edit this project.');
        }
        document.getElementById('name').value = project.name || '';
        document.getElementById('description').value = project.description || '';
    } catch (err) {
        errorDiv.style.display = 'block';
        errorDiv.textContent = err.message;
        form.style.display = 'none';
        return;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('name').value.trim();
        const description = document.getElementById('description').value.trim();
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';

        try {
            const resp = await fetch(`http://localhost:8080/api/projects/${projectId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, description })
            });
            if (!resp.ok) {
                const errorObj = await resp.json();
                throw new Error(errorObj.message || 'Failed to update project.');
            }
            window.location.href = '/projects.html';
        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    });
});
