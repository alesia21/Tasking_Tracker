// projectTasks.js
document.addEventListener('DOMContentLoaded', async () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const params = new URLSearchParams(window.location.search);
    const projectId = params.get('projectId');
    const errorDiv = document.getElementById('errorMsg');
    const heading = document.getElementById('projectNameHeading');
    const backBtn = document.getElementById('backToProjects');
    const createBtn = document.getElementById('createTaskBtn');
    const searchInput = document.getElementById('searchInput');
    const applySearchBtn = document.getElementById('applySearchBtn');
    const statusFilter = document.getElementById('statusFilter');
    const applyFilterBtn = document.getElementById('applyFilterBtn');
    const tasksBody = document.getElementById('tasksBody');

    if (!projectId) {
        window.location.href = '/projects.html';
        return;
    }

    backBtn.addEventListener('click', () => {
        window.location.href = '/projects.html';
    });
    createBtn.addEventListener('click', () => {
        window.location.href = `/createTask.html?projectId=${projectId}`;
    });

    // 1) Fetch project to verify ownership and set heading
    try {
        const projResp = await fetch(`http://localhost:8080/api/projects/${projectId}`);
        if (!projResp.ok) throw new Error('Project not found.');
        const project = await projResp.json();
        if (!project.owner || project.owner.id !== Number(userId)) {
            throw new Error('You do not have access to this project.');
        }
        heading.textContent = `Tasks for “${project.name}”`;
    } catch (err) {
        errorDiv.style.display = 'block';
        errorDiv.textContent = err.message;
        document.querySelector('.card-table').style.display = 'none';
        return;
    }

    // 2) Fetch & render tasks (with optional “query” for status or search)
    async function loadTasks({ status = '', search = '' } = {}) {
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';
        tasksBody.innerHTML = '';

        let url;
        if (search) {
            // Search: GET /api/tasks/search?projectId=&search=
            url = `http://localhost:8080/api/tasks/search?projectId=${projectId}&search=${encodeURIComponent(search)}`;
        } else {
            // Normal: GET /api/projects/{projectId}/tasks?status=&page=0&size=50
            url = `http://localhost:8080/api/projects/${projectId}/tasks`;
            if (status) url += `?status=${status}&page=0&size=50`;
        }

        try {
            const resp = await fetch(url);
            if (!resp.ok) throw new Error('Failed to fetch tasks.');
            const data = await resp.json();
            const tasks = Array.isArray(data) ? data : (data.content || []);
            if (tasks.length === 0) {
                tasksBody.innerHTML = `<tr><td colspan="6" style="text-align:center;">No tasks found.</td></tr>`;
                return;
            }

            tasks.forEach((t) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
          <td>${t.title}</td>
          <td>${t.status}</td>
          <td>${t.priority}</td>
          <td>${t.dueDate}</td>
          <td>${t.assignee?.username || '—'}</td>
          <td>
            <button class="editTaskBtn" data-id="${t.id}">Edit</button>
            <button class="deleteTaskBtn" data-id="${t.id}">Delete</button>
            <button class="activityBtn" data-id="${t.id}">Activity</button>
          </td>
        `;
                tasksBody.appendChild(tr);
            });

            // Attach Edit / Delete / Activity event listeners
            document.querySelectorAll('.editTaskBtn').forEach((btn) => {
                btn.addEventListener('click', () => {
                    const taskId = btn.getAttribute('data-id');
                    window.location.href = `/editTask.html?projectId=${projectId}&taskId=${taskId}`;
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
            loadTasks({ status: statusFilter.value, search: searchInput.value.trim() });
        } catch (err) {
            alert(err.message);
        }
    }

    applyFilterBtn.addEventListener('click', (e) => {
        e.preventDefault();
        loadTasks({ status: statusFilter.value, search: '' });
    });

    applySearchBtn.addEventListener('click', (e) => {
        e.preventDefault();
        loadTasks({ status: '', search: searchInput.value.trim() });
    });

    // Initial load (no filter, no search)
    loadTasks({});
});
