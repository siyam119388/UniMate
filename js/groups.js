// groups.js - study group list, detail and creation

async function loadGroups() {
  setLoading('groupList');
  try {
    const list = await API.get(api('groups'));
    const box = document.getElementById('groupList');

    box.innerHTML = list.length
      ? list.map(g => `
        <a class="card link" href="group-detail.html?id=${g.groupId}">
          <div class="card-row">
            <div class="icon">G</div>
            <div class="grow">
              <h3>${esc(g.name)}</h3>
              <div style="margin-top:5px">
                <span class="tag">${esc(g.courseCode || 'General')}</span>
                <span class="tag grey">${esc(g.university)}</span>
              </div>
            </div>
          </div>
          <p style="font-size:12px;margin-top:9px">${esc(g.description || '')}</p>
          <div class="small" style="margin-top:8px">
            ${g.memberCount} / ${g.maxMembers} members
          </div>
        </a>`).join('')
      : '<div class="empty">No study group yet. Create the first one.</div>';
  } catch (err) {
    setEmpty('groupList', err.message);
  }
}

let groupId = null;

async function loadGroupDetail() {
  groupId = param('id');
  try {
    const g = await API.get(api('groups'), { action: 'detail', id: groupId });
    if (!g) { setEmpty('about', 'Group not found.'); return; }

    document.getElementById('name').textContent    = g.name;
    document.getElementById('meta').textContent    = g.university + ' - ' + (g.courseCode || 'General');
    document.getElementById('members').textContent = g.memberCount;
    document.getElementById('capacity').textContent = g.maxMembers;
    document.getElementById('about').textContent   = g.description || 'No description.';

    if (g.memberCount >= g.maxMembers) {
      const b = document.getElementById('joinBtn');
      b.disabled = true;
      b.textContent = 'This group is full';
    }
  } catch (err) {
    setEmpty('about', err.message);
  }
}

async function joinGroup() {
  const btn = document.getElementById('joinBtn');
  btn.disabled = true;
  try {
    const r = await API.post(api('groups'), { action: 'join', groupId: groupId });
    btn.textContent = r.message;
    btn.classList.add('ghost');
  } catch (err) {
    showAlert('msg', err.message);
    btn.disabled = false;
  }
}

async function createGroup(e) {
  e.preventDefault();
  const btn = document.getElementById('submitBtn');
  btn.disabled = true;

  try {
    await API.post(api('groups'), {
      action:      'create',
      name:        document.getElementById('name').value.trim(),
      courseCode:  document.getElementById('courseCode').value.trim(),
      university:  document.getElementById('university').value,
      description: document.getElementById('description').value.trim(),
      maxMembers:  document.getElementById('maxMembers').value
    });
    location.href = 'groups.html';
  } catch (err) {
    showAlert('msg', err.message);
    btn.disabled = false;
  }
}
