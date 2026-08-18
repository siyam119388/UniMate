// admin.js - the admin console

async function loadAdmin() {
  try {
    const d = await API.get(api('admin'));

    document.getElementById('totalUsers').textContent   = d.totalUsers;
    document.getElementById('pendingCount').textContent = d.pendingUsers.length;
    document.getElementById('itemCount').textContent    = d.pendingItems.length;

    const users = document.getElementById('pendingUsers');
    users.innerHTML = d.pendingUsers.length
      ? d.pendingUsers.map(u => `
        <div class="card">
          <div class="card-row">
            <div class="avatar light">${initials(u.name)}</div>
            <div class="grow">
              <h3>${esc(u.name)}</h3>
              <div class="small">${esc(u.email)} - ${esc(u.role || 'STUDENT')}</div>
            </div>
          </div>
          <div class="btn-row" style="margin-top:11px">
            <button class="btn sm ghost" style="flex:1"
                    onclick="act('rejectUser', ${u.userId})">Reject</button>
            <button class="btn sm" style="flex:1"
                    onclick="act('approveUser', ${u.userId})">Approve</button>
          </div>
        </div>`).join('')
      : '<div class="empty">No account is waiting.</div>';

    const items = document.getElementById('pendingItems');
    items.innerHTML = d.pendingItems.length
      ? d.pendingItems.map(i => `
        <div class="card">
          <div class="card-row">
            <div class="icon">M</div>
            <div class="grow">
              <h3>${esc(i.title)}</h3>
              <div class="small">${money(i.price)} - ${esc(i.sellerName || '')}</div>
            </div>
          </div>
          <div class="btn-row" style="margin-top:11px">
            <button class="btn sm ghost" style="flex:1"
                    onclick="act('removeItem', ${i.itemId})">Remove</button>
            <button class="btn sm" style="flex:1"
                    onclick="act('approveItem', ${i.itemId})">Approve</button>
          </div>
        </div>`).join('')
      : '<div class="empty">No listing is waiting.</div>';

  } catch (err) {
    setEmpty('pendingUsers', err.message);
    setEmpty('pendingItems', err.message);
  }
}

async function act(action, id) {
  try {
    await API.post(api('admin'), { action: action, id: id });
    loadAdmin();                       // refresh both queues
  } catch (err) {
    alert(err.message);
  }
}

function showAdminTab(name, el) {
  document.querySelectorAll('.tabs button').forEach(b => b.classList.remove('on'));
  el.classList.add('on');
  ['usersTab', 'itemsTab'].forEach(id =>
    document.getElementById(id).classList.toggle('hidden', id !== name));
}
