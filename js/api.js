// api.js - one place that talks to the servlets

const API = {

  // POST with form fields, e.g. API.post('login', {email, password})
  async post(path, fields) {
    const body = new URLSearchParams(fields).toString();
    const res = await fetch(path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body
    });
    return API.handle(res);
  },

  // POST a file (multipart)
  async upload(path, formData) {
    const res = await fetch(path, { method: 'POST', body: formData });
    return API.handle(res);
  },

  // GET, e.g. API.get('courses', {action: 'detail', id: 4})
  async get(path, params) {
    const qs = params ? '?' + new URLSearchParams(params).toString() : '';
    const res = await fetch(path + qs);
    return API.handle(res);
  },

  async handle(res) {
    let data;
    try { data = await res.json(); } catch (e) { data = {}; }

    if (res.status === 401) {          // session gone - back to login
      location.href = base() + 'index.html';
      return Promise.reject(new Error('Not logged in'));
    }
    if (!res.ok) {
      throw new Error(data.error || 'Something went wrong');
    }
    return data;
  }
};

// works whether the page sits in the root or in a sub-folder
function base() {
  return location.pathname.split('/').slice(0, 2).join('/') + '/';
}

function api(path) {
  return base() + path;
}
