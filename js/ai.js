// ai.js - the study assistant chat

async function askAI(e) {
  e.preventDefault();

  const input = document.getElementById('question');
  const question = input.value.trim();
  if (!question) return;

  const chat = document.getElementById('chat');
  chat.insertAdjacentHTML('beforeend',
    `<div class="bubble me">${esc(question)}</div>`);
  input.value = '';
  chat.scrollTop = chat.scrollHeight;

  const waitId = 'w' + Date.now();
  chat.insertAdjacentHTML('beforeend',
    `<div class="bubble them" id="${waitId}">Looking through your course materials...</div>`);

  try {
    const r = await API.post(api('ai'), { question: question });
    document.getElementById(waitId).remove();

    let html = `<div class="bubble them">${esc(r.answer)}</div>`;
    if (r.source) {
      html += `
        <div class="card" style="max-width:78%">
          <div class="card-row">
            <div class="icon file">${esc((r.source.type || 'FILE').slice(0, 4))}</div>
            <div class="grow">
              <h3>${esc(r.source.title)}</h3>
              <div class="small">Source material</div>
            </div>
            <a class="btn sm ghost"
               href="${api('materials')}?action=download&id=${r.source.materialId}">Open</a>
          </div>
        </div>`;
    }
    chat.insertAdjacentHTML('beforeend', html);
    chat.scrollTop = chat.scrollHeight;

  } catch (err) {
    document.getElementById(waitId).textContent = err.message;
  }
}

function askSuggested(text) {
  document.getElementById('question').value = text;
  askAI(new Event('submit'));
}
