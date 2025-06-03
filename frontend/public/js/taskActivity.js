// taskActivity.js
document.addEventListener('DOMContentLoaded', async () => {
    ensureLoggedIn();
    renderNavBar();

    const userId = getUserId();
    const params = new URLSearchParams(window.location.search);
    const taskId = params.get('taskId');
    const errorDiv = document.getElementById('errorMsg');
    const heading = document.getElementById('activityHeading');
    const backBtn = document.getElementById('backBtn');
    const activityBody = document.getElementById('activityBody');

    if (!taskId) {
        errorDiv.style.display = 'block';
        errorDiv.textContent = 'No task specified.';
        return;
    }

    // “← Back” button just goes back in history
    backBtn.addEventListener('click', (e) => {
        e.preventDefault();
        window.history.back();
    });

    // 1) Fetch the task itself to verify permissions & set the heading
    try {
        const tResp = await fetch(`http://localhost:8080/api/tasks/${taskId}`);
        if (!tResp.ok) throw new Error('Task not found.');
        const task = await tResp.json();
        const isOwner = task.project?.owner?.id === Number(userId);
        const isAssignee = task.assignee?.id === Number(userId);
        if (!isOwner && !isAssignee) {
            throw new Error('You do not have permission to view this activity.');
        }
        heading.textContent = `Activity for “${task.title}”`;
    } catch (err) {
        errorDiv.style.display = 'block';
        errorDiv.textContent = err.message;
        document.querySelector('.card-table').style.display = 'none';
        return;
    }

    // 2) Fetch the list of TaskActivity entries
    try {
        const actResp = await fetch(`http://localhost:8080/api/tasks/${taskId}/activities`);
        if (actResp.status === 404) {
            // No mapping or no records—show a friendly message
            activityBody.innerHTML = `
        <tr>
          <td colspan="4" style="text-align:center; color:#555;">No activity logged for this task.</td>
        </tr>`;
            return;
        }
        if (!actResp.ok) {
            throw new Error('Failed to fetch activity.');
        }

        const activityList = await actResp.json();
        console.log('Fetched activityList:', activityList);
        // If the array is empty, show “No activity”
        if (!Array.isArray(activityList) || activityList.length === 0) {
            activityBody.innerHTML = `
        <tr>
          <td colspan="4" style="text-align:center; color:#555;">No activity logged for this task.</td>
        </tr>`;
            return;
        }

        // 3) Render each entry
        activityList.forEach((entry) => {
            // Inspect `entry`’s shape in the console to confirm these fields exist:
            console.log('Single entry:', entry);
            // Use entry.createdAt (timestamp), entry.action, entry.performedBy.username
            const timestamp = entry.createdAt
                ? new Date(entry.createdAt).toLocaleString()
                : '—';
            const action = entry.action || '—';
            // Corrected: use `entry.performedBy.username` instead of `performedByUser`
            const performedBy = entry.performedBy
                ? entry.performedBy.username
                : '—';
            // If you have a `details` column in your TaskActivity entity, adjust accordingly.
            // Otherwise leave it blank:
            const details = entry.details || '';

            const tr = document.createElement('tr');
            tr.innerHTML = `
        <td>${timestamp}</td>
        <td>${action}</td>
        <td>${performedBy}</td>
        <td>${details}</td>
      `;
            activityBody.appendChild(tr);
        });
    } catch (err) {
        errorDiv.style.display = 'block';
        errorDiv.textContent = err.message;
    }
});
