// auth.js - login, signup and logout

async function doLogin(e) {
  e.preventDefault();
  hideAlert('msg');

  const btn = document.getElementById('submitBtn');
  btn.disabled = true;
  btn.textContent = 'Logging in...';

  try {
    const data = await API.post(api('login'), {
      email:    document.getElementById('email').value.trim(),
      password: document.getElementById('password').value
    });
    sessionStorage.setItem('name', data.name);
    sessionStorage.setItem('role', data.role);
    location.href = base() + data.redirect;      // the server decides where
  } catch (err) {
    showAlert('msg', err.message);
    btn.disabled = false;
    btn.textContent = 'Log in';
  }
}

async function doSignup(e) {
  e.preventDefault();
  hideAlert('msg');

  const password = document.getElementById('password').value;
  if (password !== document.getElementById('confirm').value) {
    showAlert('msg', 'The two passwords do not match');
    return;
  }
  if (password.length < 8) {
    showAlert('msg', 'Use at least 8 characters');
    return;
  }

  const btn = document.getElementById('submitBtn');
  btn.disabled = true;
  btn.textContent = 'Creating...';

  try {
    await API.post(api('register'), {
      name:       document.getElementById('name').value.trim(),
      email:      document.getElementById('email').value.trim(),
      password:   password,
      role:       document.getElementById('role').value,
      university: document.getElementById('university').value,
      department: document.getElementById('department').value,
      studentId:  document.getElementById('studentId').value.trim(),
      semester:   'Summer 2026'
    });
    showAlert('msg', 'Account created. You can log in now.', 'ok');
    setTimeout(() => location.href = base() + 'index.html', 1200);
  } catch (err) {
    showAlert('msg', err.message);
    btn.disabled = false;
    btn.textContent = 'Create account';
  }
}

async function doLogout() {
  try { await API.get(api('logout')); } catch (e) {}
  sessionStorage.clear();
  location.href = base() + 'index.html';
}

// hide the student ID box when signing up as an instructor
function roleChanged() {
  const isStudent = document.getElementById('role').value === 'STUDENT';
  document.getElementById('studentIdField').classList.toggle('hidden', !isStudent);
}
