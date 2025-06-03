// userTasks.js
document.addEventListener('DOMContentLoaded', async () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const errorDiv = document.getElementById('errorMsg');
    const userTasksBody = document.getElementById('userTasksBody');

    async function loadUserTasks() {
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';
        userTasksBody.innerHTML = '';

        try {
            const resp = await fetch(`http://localhost:8080/api/users/${userId}/tasks`);
            if (!resp.ok) throw new Error('Failed to fetch your tasks.');
            const tasks = await resp.json(); // expect array

            if (!Array.isArray(tasks) || tasks.length === 0) {
                userTasksBody.innerHTML = `<tr><td colspan="6" style="text-align:center;">No tasks assigned to you.</td></tr>`;
                return;
            }

            tasks.forEach((t) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
          <td>${t.project?.name || '—'}</td>
          <td>${t.title}</td>
          <td>${t.status}</td>
          <td>${t.priority}</td>
          <td>${t.dueDate}</td>
          <td>
            <button class="editTaskBtn" data-id="${t.id}">Edit</button>
            <button class="deleteTaskBtn" data-id="${t.id}">Delete</button>
            <button class="activityBtn" data-id="${t.id}">Activity</button>
          </td>
        `;
                userTasksBody.appendChild(tr);
            });

            // Attach event listeners
            document.querySelectorAll('.editTaskBtn').forEach((btn) => {
                btn.addEventListener('click', () => {
                    const taskId = btn.getAttribute('data-id');
                    // Need to fetch task to get projectId
                    fetch(`http://localhost:8080/api/tasks/${taskId}`)
                        .then((r) => r.json())
                        .then((task) => {
                            window.location.href = `/editTask.html?projectId=${task.project.id}&taskId=${task.id}`;
                        });
                });
            });
            document.querySelectorAll('.deleteTaskBtn').forEach((btn) => {
                btn.addEventListener('click', () => deleteTask(btn.getAttribute('data-id')));
            });
            document.querySelectorAll('.activityBtn').forEach((btn) => {
                btn.addEventListener('click', () => {
                    const taskId = btn.getAttribute('data-id');
                    window.location.href = `/taskActivity.html?taskId=${taskId}`;
                });
            });

        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    }

    async function deleteTask(taskId) {
        if (!confirm('Are you sure you want to delete this task?')) return;
        try {
            const resp = await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
                method: 'DELETE'
            });
            if (!resp.ok) {
                const errorObj = await resp.json();
                throw new Error(errorObj.message || 'Failed to delete task.');
            }
            loadUserTasks();
        } catch (err) {
            alert(err.message);
        }
    }

    // Initial load
    loadUserTasks();
});
