// profile.js - the profile screen

async function loadProfile() {
  try {
    const p = await API.get(api('profile'));

    document.getElementById('name').textContent   = p.name;
    document.getElementById('avatar').textContent = initials(p.name);
    document.getElementById('meta').textContent   = (p.department || '') + ' - ' + (p.university || '');
    document.getElementById('email').textContent  = p.email;
    document.getElementById('role').textContent   = p.role;
    document.getElementById('university').textContent = p.university || '-';
    document.getElementById('department').textContent = p.department || '-';

    if (p.studentId) {
      document.getElementById('studentId').textContent = p.studentId;
      document.getElementById('semester').textContent  = p.semester || '-';
    } else {
      document.getElementById('idRow').classList.add('hidden');
      document.getElementById('semRow').classList.add('hidden');
    }

    const courses = p.courses || [];
    document.getElementById('profileCourses').innerHTML = courses.length
      ? courses.map(c => `
        <div class="card">
          <div class="card-row">
            <div class="icon">B</div>
            <div class="grow">
              <h3>${esc(c.code)} - ${esc(c.title)}</h3>
              <div class="small">${c.credits} credits</div>
            </div>
          </div>
        </div>`).join('')
      : '<div class="empty">No course yet.</div>';

    const groups = p.groups || [];
    document.getElementById('profileGroups').innerHTML = groups.length
      ? groups.map(g => `
        <div class="card">
          <div class="card-row">
            <div class="icon">G</div>
            <div class="grow">
              <h3>${esc(g.name)}</h3>
              <div class="small">${g.memberCount} members</div>
            </div>
          </div>
        </div>`).join('')
      : '<div class="empty">No group yet.</div>';

  } catch (err) {
    setEmpty('profileCourses', err.message);
  }
}
