// market.js - marketplace list, item detail and selling

async function loadMarket() {
  setLoading('itemList');
  try {
    const list = await API.get(api('marketplace'));
    const box = document.getElementById('itemList');

    if (!list.length) {
      box.innerHTML = '<div class="empty">Nothing is listed right now.</div>';
      return;
    }
    document.getElementById('count').textContent = list.length + ' items';

    box.innerHTML = list.map(i => `
      <a class="tile-card" href="product.html?id=${i.itemId}">
        <div class="thumb">${thumb(i)}</div>
        <div class="body">
          <h3 style="font-size:12px;line-height:1.35">${esc(i.title)}</h3>
          <span class="price">${money(i.price)}</span>
          <div class="small" style="margin-top:4px">${esc(i.sellerName || '')}</div>
        </div>
      </a>`).join('');
  } catch (err) {
    setEmpty('itemList', err.message);
  }
}

function photoUrl(name) {
  return api('marketplace') + '?action=photo&name=' + encodeURIComponent(name);
}

function thumb(i) {
  return i.photo
    ? `<img src="${photoUrl(i.photo)}" alt="" style="width:100%;height:100%;object-fit:cover">`
    : categoryIcon(i.category);
}

function categoryIcon(c) {
  if (c === 'EQUIPMENT') return 'E';
  if (c === 'NOTES')     return 'N';
  if (c === 'OTHER')     return 'O';
  return 'B';
}

async function loadProduct() {
  try {
    const i = await API.get(api('marketplace'), { action: 'detail', id: param('id') });
    if (!i) { setEmpty('body', 'Item not found.'); return; }

    const box = document.getElementById('thumb');
    if (i.photo) {
      box.innerHTML = `<img src="${photoUrl(i.photo)}" alt=""
                            style="width:100%;height:100%;object-fit:cover">`;
    } else {
      box.textContent = categoryIcon(i.category);
    }
    document.getElementById('title').textContent     = i.title;
    document.getElementById('price').textContent     = money(i.price);
    document.getElementById('category').textContent  = i.category;
    document.getElementById('condition').textContent = (i.itemCondition || '').replace('_', ' ');
    document.getElementById('pickup').textContent    = i.pickupPoint || 'Not stated';
    document.getElementById('desc').textContent      = i.description || '';
    document.getElementById('seller').textContent    = i.sellerName || 'Unknown';
    document.getElementById('sellerAvatar').textContent = initials(i.sellerName);
  } catch (err) {
    setEmpty('body', err.message);
  }
}

async function sellItem(e) {
  e.preventDefault();
  hideAlert('msg');

  const price = document.getElementById('price').value;
  if (!price || Number(price) <= 0) {
    showAlert('msg', 'Enter a price above zero');
    return;
  }

  const btn = document.getElementById('submitBtn');
  btn.disabled = true;
  btn.textContent = 'Posting...';

  // multipart, because the photo is a file
  const form = new FormData();
  form.append('title',       document.getElementById('title').value.trim());
  form.append('description', document.getElementById('description').value.trim());
  form.append('category',    document.getElementById('category').value);
  form.append('price',       price);
  form.append('condition',   document.getElementById('condition').value);
  form.append('pickupPoint', document.getElementById('pickupPoint').value.trim());

  const file = document.getElementById('photo').files[0];
  if (file) form.append('photo', file);

  try {
    const r = await API.upload(api('marketplace'), form);
    showAlert('msg', r.message, 'ok');
    setTimeout(() => location.href = 'marketplace.html', 1400);
  } catch (err) {
    showAlert('msg', err.message);
    btn.disabled = false;
    btn.textContent = 'Post listing';
  }
}
