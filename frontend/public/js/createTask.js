// createTask.js
document.addEventListener('DOMContentLoaded', async () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const params = new URLSearchParams(window.location.search);
    const projectId = params.get('projectId');
    const errorDiv = document.getElementById('errorMsg');
    const heading = document.getElementById('taskFormHeading');
    const form = document.getElementById('createTaskForm');
    const cancelBtn = document.getElementById('cancelBtn');

    if (!projectId) {
        window.location.href = '/projects.html';
        return;
    }

    cancelBtn.addEventListener('click', () => {
        window.location.href = `/projectTasks.html?projectId=${projectId}`;
    });

    // Pre‐verify project ownership and set heading
    try {
        const resp = await fetch(`http://localhost:8080/api/projects/${projectId}`);
        if (!resp.ok) throw new Error('Project not found.');
        const project = await resp.json();
        if (!project.owner || project.owner.id !== Number(userId)) {
            throw new Error('You do not have permission to add tasks to this project.');
        }
        heading.textContent = `Create Task for “${project.name}”`;
    } catch (err) {
        errorDiv.style.display = 'block';
        errorDiv.textContent = err.message;
        form.style.display = 'none';
        return;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const title = document.getElementById('title').value.trim();
        const description = document.getElementById('description').value.trim();
        const status = document.getElementById('status').value;
        const priority = document.getElementById('priority').value;
        const dueDate = document.getElementById('dueDate').value;
        let assigneeId = document.getElementById('assigneeId').value.trim();

        if (!assigneeId) assigneeId = userId; // default to self

        errorDiv.style.display = 'none';
        errorDiv.textContent = '';

        try {
            const resp = await fetch(`http://localhost:8080/api/projects/${projectId}/tasks`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    title,
                    description,
                    status,
                    priority,
                    dueDate,
                    assignee: { id: Number(assigneeId) },
                    project: { id: Number(projectId) }
                })
            });
            if (!resp.ok) {
                const errorObj = await resp.json();
                throw new Error(errorObj.message || 'Failed to create task.');
            }
            window.location.href = `/projectTasks.html?projectId=${projectId}`;
        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    });
});
