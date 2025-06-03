// projects.js
document.addEventListener('DOMContentLoaded', async () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const errorDiv = document.getElementById('errorMsg');
    const projectListTbody = document.getElementById('projectList');
    const createBtn = document.getElementById('createProjectBtn');

    createBtn.addEventListener('click', () => {
        window.location.href = '/createProject.html';
    });

    async function loadProjects() {
        errorDiv.style.display = 'none';
        errorDiv.textContent = '';
        projectListTbody.innerHTML = '';

        try {
            // Fetch all projects (pagination is optional—assuming backend returns Page<Project>)
            const resp = await fetch('http://localhost:8080/api/projects');
            if (!resp.ok) throw new Error('Failed to fetch projects.');

            const data = await resp.json();
            const allProjects = Array.isArray(data) ? data : (data.content || []);
            // Filter by owner.id === userId
            const myProjects = allProjects.filter((p) => p.owner && p.owner.id === Number(userId));

            if (myProjects.length === 0) {
                projectListTbody.innerHTML = `<tr><td colspan="4" style="text-align:center;">You have no projects yet.</td></tr>`;
                return;
            }

            myProjects.forEach((proj) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
          <td>${proj.name}</td>
          <td>${proj.description || '—'}</td>
          <td>${new Date(proj.createdAt).toLocaleDateString()}</td>
          <td>
            <button class="editBtn" data-id="${proj.id}">Edit</button>
            <button class="deleteBtn" data-id="${proj.id}">Delete</button>
            <button class="tasksBtn" data-id="${proj.id}">Tasks</button>
          </td>
        `;
                projectListTbody.appendChild(tr);
            });

            // Attach event listeners for Edit/Delete/Tasks buttons
            document.querySelectorAll('.editBtn').forEach((btn) => {
                btn.addEventListener('click', () => {
                    const projId = btn.getAttribute('data-id');
                    window.location.href = `/editProject.html?projectId=${projId}`;
                });
            });
            document.querySelectorAll('.deleteBtn').forEach((btn) => {
                btn.addEventListener('click', () => deleteProject(btn.getAttribute('data-id')));
            });
            document.querySelectorAll('.tasksBtn').forEach((btn) => {
                btn.addEventListener('click', () => {
                    const projId = btn.getAttribute('data-id');
                    window.location.href = `/projectTasks.html?projectId=${projId}`;
                });
            });

        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.textContent = err.message;
        }
    }

    async function deleteProject(projectId) {
        if (!confirm('Are you sure you want to delete this project?')) return;

        try {
            const resp = await fetch(`http://localhost:8080/api/projects/${projectId}`, {
                method: 'DELETE'
            });
            if (!resp.ok) {
                const errorObj = await resp.json();
                throw new Error(errorObj.message || 'Failed to delete project.');
            }
            loadProjects(); // refresh list
        } catch (err) {
            alert(err.message);
        }
    }

    // Initial load
    loadProjects();
});
