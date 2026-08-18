// courses.js - course list, course detail, materials

// ---------- dashboard ----------
async function loadDashboard() {
  document.getElementById('userName').textContent = sessionStorage.getItem('name') || 'there';

  try {
    const [courses, groups] = await Promise.all([
      API.get(api('courses'), { action: 'mine' }),
      API.get(api('groups'),  { action: 'mine' })
    ]);

    document.getElementById('statCourses').textContent = courses.length;
    document.getElementById('statGroups').textContent  = groups.length;

    const box = document.getElementById('myCourses');
    if (!courses.length) {
      box.innerHTML = '<div class="empty">You have not enrolled in a course yet.</div>';
    } else {
      box.innerHTML = courses.slice(0, 3).map(c => `
        <a class="card link" href="course-detail.html?id=${c.courseId}">
          <div class="card-row">
            <div class="icon">B</div>
            <div class="grow">
              <h3>${esc(c.code)} - ${esc(c.title)}</h3>
              <div class="small">${esc(c.instructorName || 'No instructor')}</div>
            </div>
          </div>
        </a>`).join('');
    }

    const gbox = document.getElementById('myGroups');
    gbox.innerHTML = groups.length
      ? groups.slice(0, 2).map(g => `
        <a class="card link" href="group-detail.html?id=${g.groupId}">
          <div class="card-row">
            <div class="icon">G</div>
            <div class="grow">
              <h3>${esc(g.name)}</h3>
              <div class="small">${esc(g.university)} - ${g.memberCount} members</div>
            </div>
          </div>
        </a>`).join('')
      : '<div class="empty">You have not joined a group yet.</div>';

  } catch (err) {
    setEmpty('myCourses', err.message);
  }
}

// ---------- course list ----------
async function loadCourses(keyword) {
  setLoading('courseList');
  try {
    const list = await API.get(api('courses'), { q: keyword || '' });
    const box = document.getElementById('courseList');

    if (!list.length) {
      box.innerHTML = '<div class="empty">No course matched that search.</div>';
      return;
    }
    document.getElementById('count').textContent = list.length + ' courses';

    box.innerHTML = list.map(c => `
      <a class="card link" href="course-detail.html?id=${c.courseId}">
        <div>
          <span class="tag">${esc(c.code)}</span>
          <span class="tag grey">${c.credits} cr</span>
          <span class="tag ${diffClass(c.difficulty)}">${esc(c.difficulty)}</span>
        </div>
        <h3 style="margin-top:7px">${esc(c.title)}</h3>
        <div class="small" style="margin-top:3px">${esc(c.instructorName || 'No instructor')}</div>
        <div class="small" style="margin-top:8px">${c.enrolledCount} enrolled</div>
      </a>`).join('');
  } catch (err) {
    setEmpty('courseList', err.message);
  }
}

function diffClass(d) {
  if (d === 'EASY') return 'green';
  if (d === 'HARD') return 'red';
  return 'orange';
}

function searchCourses(e) {
  e.preventDefault();
  loadCourses(document.getElementById('q').value.trim());
}

// ---------- course detail ----------
let courseId = null;

async function loadCourseDetail() {
  courseId = param('id');
  try {
    const c = await API.get(api('courses'), { action: 'detail', id: courseId });
    if (!c) { setEmpty('overview', 'Course not found.'); return; }

    document.getElementById('code').textContent   = c.code;
    document.getElementById('title').textContent  = c.title;
    document.getElementById('teacher').textContent = c.instructorName || 'No instructor';
    document.getElementById('credits').textContent = c.credits + ' credits';
    document.getElementById('level').textContent   = c.difficulty;
    document.getElementById('enrolled').textContent = c.enrolledCount;
    document.getElementById('about').textContent   = c.description || 'No description yet.';
    document.getElementById('prereq').textContent  = c.prerequisites || 'None';

    loadMaterials();
  } catch (err) {
    setEmpty('overview', err.message);
  }
}

async function loadMaterials() {
  try {
    const list = await API.get(api('materials'), { courseId: courseId });
    const box = document.getElementById('materials');

    box.innerHTML = list.length
      ? list.map(m => `
        <div class="card">
          <div class="card-row">
            <div class="icon file">${esc((m.type || 'FILE').slice(0, 4))}</div>
            <div class="grow">
              <h3>${esc(m.title)}</h3>
              <div class="small">${esc(m.uploaderName || 'Unknown')} - ${m.downloads} downloads</div>
            </div>
            <a class="btn sm ghost" href="${api('materials')}?action=download&id=${m.materialId}">Get</a>
          </div>
        </div>`).join('')
      : '<div class="empty">No material uploaded yet.</div>';
  } catch (err) {
    setEmpty('materials', err.message);
  }
}

async function enroll() {
  const btn = document.getElementById('enrollBtn');
  btn.disabled = true;
  try {
    const r = await API.post(api('courses'), { courseId: courseId });
    btn.textContent = r.message;
    btn.classList.add('ghost');
  } catch (err) {
    showAlert('msg', err.message);
    btn.disabled = false;
  }
}

function showTab(name, el) {
  document.querySelectorAll('.tabs button').forEach(b => b.classList.remove('on'));
  el.classList.add('on');
  ['overview', 'materialsTab'].forEach(id =>
    document.getElementById(id).classList.toggle('hidden', id !== name));
}
