// board.js - internship, hackathon and scholarship board

async function loadBoard(type) {
  setLoading('oppList');
  try {
    const list = await API.get(api('opportunities'), type ? { type: type } : null);
    const box = document.getElementById('oppList');

    box.innerHTML = list.length
      ? list.map(o => {
          const left = daysLeft(o.deadline);
          const closing = left !== null && left <= 7 && left >= 0;
          const closed  = left !== null && left < 0;
          return `
        <div class="card ${closing ? 'warn' : ''}" ${closed ? 'style="opacity:.55"' : ''}>
          <div class="card-row">
            <div class="icon dark">${esc((o.type || 'X')[0])}</div>
            <div class="grow">
              <h3>${esc(o.title)}</h3>
              <div class="small">${esc(o.company || '')}</div>
            </div>
          </div>
          <div style="margin-top:9px">
            <span class="tag">${esc(o.type)}</span>
            ${o.stipend ? `<span class="tag green">${esc(o.stipend)}</span>` : ''}
          </div>
          <div class="card-row" style="margin-top:11px">
            <span class="small">
              ${closed ? 'Closed' : (closing ? left + ' days left' : 'Closes ' + shortDate(o.deadline))}
            </span>
            ${o.applyLink && !closed
              ? `<a class="btn sm" style="margin-left:auto" target="_blank"
                    href="${esc(o.applyLink)}">Apply</a>` : ''}
          </div>
        </div>`;
        }).join('')
      : '<div class="empty">Nothing posted in this category yet.</div>';
  } catch (err) {
    setEmpty('oppList', err.message);
  }
}

function filterBoard(type, el) {
  document.querySelectorAll('.chip').forEach(c => c.classList.remove('on'));
  el.classList.add('on');
  loadBoard(type);
}
