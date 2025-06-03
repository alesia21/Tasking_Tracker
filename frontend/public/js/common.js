// common.js


function getUserId() {
    return localStorage.getItem('userId');
}

function ensureLoggedIn() {
    const userId = getUserId();
    if (!userId) {
        window.location.href = '/login.html';
    }
}

/**
 * Render the header nav bar dynamically:
 * • If logged in (userId exists): show Dashboard, Projects, Tasks, Logout
 * • Else: show Login, Register
 */
function renderNavBar() {
    const userId = getUserId();
    const navContainer = document.querySelector('.nav-buttons');
    if (!navContainer) return;

    navContainer.innerHTML = ''; // clear out existing

    if (userId) {
        // Dashboard (we’ll map “Dashboard” → /projects.html for now)
        const dashBtn = document.createElement('button');
        dashBtn.textContent = 'Dashboard';
        dashBtn.addEventListener('click', () => {
            window.location.href = '/projects.html';
        });
        navContainer.appendChild(dashBtn);

        // Projects
        const projBtn = document.createElement('button');
        projBtn.textContent = 'Projects';
        projBtn.addEventListener('click', () => {
            window.location.href = '/projects.html';
        });
        navContainer.appendChild(projBtn);

        // Tasks (we’ll map “Tasks” → /userTasks.html?userId={userId})
        const tasksBtn = document.createElement('button');
        tasksBtn.textContent = 'Tasks';
        tasksBtn.addEventListener('click', () => {
            window.location.href = `/userTasks.html`;
        });
        navContainer.appendChild(tasksBtn);

        // Logout
        const logoutBtn = document.createElement('button');
        logoutBtn.textContent = 'Logout';
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('userId');
            window.location.href = '/login.html';
        });
        navContainer.appendChild(logoutBtn);
    } else {
        // Not logged in: show Login and Register buttons
        const loginBtn = document.createElement('button');
        loginBtn.textContent = 'Login';
        loginBtn.addEventListener('click', () => {
            window.location.href = '/login.html';
        });
        navContainer.appendChild(loginBtn);

        const registerBtn = document.createElement('button');
        registerBtn.textContent = 'Register';
        registerBtn.addEventListener('click', () => {
            window.location.href = '/register.html';
        });
        navContainer.appendChild(registerBtn);
    }
}
