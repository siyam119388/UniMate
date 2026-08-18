// ui.js - small helpers every page uses

// show an error or success message inside an element
function showAlert(id, message, kind) {
  const box = document.getElementById(id);
  if (!box) return;
  box.className = 'alert ' + (kind || 'error');
  box.textContent = message;
  box.classList.remove('hidden');
}

function hideAlert(id) {
  const box = document.getElementById(id);
  if (box) box.classList.add('hidden');
}

// replace an element's content with a loading line
function setLoading(id, text) {
  const el = document.getElementById(id);
  if (el) el.innerHTML = '<div class="loading">' + (text || 'Loading...') + '</div>';
}

function setEmpty(id, text) {
  const el = document.getElementById(id);
  if (el) el.innerHTML = '<div class="empty">' + text + '</div>';
}

// initials for an avatar, e.g. "Md. Ashikur Rahman" -> "MA"
function initials(name) {
  if (!name) return '?';
  const parts = name.replace(/[^A-Za-z ]/g, '').trim().split(/\s+/);
  return (parts[0][0] + (parts[1] ? parts[1][0] : '')).toUpperCase();
}

function money(value) {
  return 'BDT ' + Number(value).toLocaleString('en-US');
}

// "2026-08-20" -> "20 Aug"
function shortDate(value) {
  if (!value) return '';
  const d = new Date(value);
  if (isNaN(d)) return value;
  const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  return d.getDate() + ' ' + months[d.getMonth()];
}

function daysLeft(value) {
  const d = new Date(value);
  if (isNaN(d)) return null;
  return Math.ceil((d - new Date()) / 86400000);
}

// read ?id=5 from the address bar
function param(name) {
  return new URLSearchParams(location.search).get(name);
}

// keep the user's text safe when we build HTML by hand
function esc(text) {
  const d = document.createElement('div');
  d.textContent = text == null ? '' : text;
  return d.innerHTML;
}

// highlight the current item in the bottom bar
function markNav(name) {
  document.querySelectorAll('.nav a').forEach(a => {
    if (a.dataset.nav === name) a.classList.add('on');
  });
}
