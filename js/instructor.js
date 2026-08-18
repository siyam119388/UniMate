// instructor.js - instructor dashboard and material upload

async function loadInstructor() {
  document.getElementById('userName').textContent = sessionStorage.getItem('name') || 'Instructor';
  document.getElementById('avatar').textContent   = initials(sessionStorage.getItem('name'));

  try {
    const courses = await API.get(api('courses'), { action: 'teaching' });

    document.getElementById('statCourses').textContent = courses.length;
    document.getElementById('statStudents').textContent =
      courses.reduce((sum, c) => sum + (c.enrolledCount || 0), 0);

    // fill the course picker on the upload form
    const select = document.getElementById('courseId');
    if (courses.length) {
      select.innerHTML = courses.map(c =>
        `<option value="${c.courseId}">${esc(c.code)} - ${esc(c.title)}</option>`).join('');
    } else {
      select.innerHTML = '<option value="0">You do not own a course yet</option>';
      showAlert('msg',
        'No course is assigned to you, so uploading will not work. ' +
        'Log in as sabrina@northsouth.edu to try the upload.', 'error');
    }

    const box = document.getElementById('teachingList');
    box.innerHTML = courses.length
      ? courses.map(c => `
        <div class="card">
          <div>
            <span class="tag">${esc(c.code)}</span>
            <span class="tag green">Published</span>
          </div>
          <h3 style="margin-top:7px">${esc(c.title)}</h3>
          <div class="stats" style="margin-top:11px">
            <div><b>${c.enrolledCount}</b><span>Enrolled</span></div>
            <div><b>${c.credits}</b><span>Credits</span></div>
          </div>
        </div>`).join('')
      : '<div class="empty">You do not teach any course yet.</div>';

  } catch (err) {
    setEmpty('teachingList', err.message);
  }
}

async function uploadMaterial(e) {
  e.preventDefault();
  hideAlert('msg');

  const file = document.getElementById('file').files[0];
  if (!file) { showAlert('msg', 'Choose a file first'); return; }

  const btn = document.getElementById('submitBtn');
  btn.disabled = true;
  btn.textContent = 'Uploading...';

  const form = new FormData();
  form.append('file', file);
  form.append('courseId', document.getElementById('courseId').value);
  form.append('type',     document.getElementById('type').value);
  form.append('title',    document.getElementById('title').value.trim());

  try {
    const r = await API.upload(api('materials'), form);
    showAlert('msg', r.message, 'ok');
    document.getElementById('title').value = '';
    document.getElementById('file').value = '';
  } catch (err) {
    showAlert('msg', err.message);
  }

  btn.disabled = false;
  btn.textContent = 'Upload and publish';
}
