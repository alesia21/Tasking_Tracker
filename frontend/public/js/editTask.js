// editTask.js
document.addEventListener('DOMContentLoaded', async () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const params = new URLSearchParams(window.location.search);
    const projectId = params.get('projectId');
    const taskId = params.get('taskId');
    const errorDiv = document.getElementById('errorMsg');
    const heading = document.getElementById('taskFormHeading');
    const form = document.getElementById('editTaskForm');
    const cancelBtn = document.getElementById('cancelBtn');

    if (!projectId || !taskId) {
        window.location.href = '/projects.html';
        return;
    }

    cancelBtn.addEventListener('click', () => {
        window.location.href = `/projectTasks.html?projectId=${projectId}`;
    });

    // 1) Fetch task & verify ownership
    try {
        // Get task by ID
        const tResp = await fetch(`http://localhost:8080/api/tasks/${taskId}`);
        if (!tResp.ok) throw new Error('Task not found.');
        const task = await tResp.json();

        // Ensure user owns the project or is owner
        if (!task.project || task.project.owner.id !== Number(userId)) {
            throw new Error('You do not have permission to edit this task.');
        }
        heading.textContent = `Edit Task for “${task.project.name}”`;

        // Pre‐fill form
        document.getElementById('title').value = task.title;
        document.getElementById('description').value = task.description || '';
        document.getElementById('status').value = task.status;
        document.getElementById('priority').value = task.priority;
        document.getElementById('dueDate').value = task.dueDate;
        document.getElementById('assigneeId').value = task.assignee?.id || '';
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
        const assigneeId = document.getElementById('assigneeId').value.trim() || userId;

        errorDiv.style.display = 'none';
        errorDiv.textContent = '';

        try {
            const resp = await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    title,
                    description,
                    status,
                    priority,
                    dueDate,
                    assignee: { id: Number(assigneeId) }
                })
            });
            if (!resp.ok) {
                const errorObj = await resp.json();
                throw new Error(errorObj.message || 'Failed to update task.');
            }
            window.location.href = `/projectTasks.html?projectId=${projectId}`;
        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    });
});
