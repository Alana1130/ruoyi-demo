/**
 * 旅游小程序请求封装，对接 xinglv 后端 /travel 接口
 * 登录态：优先使用 token（Authorization），无 token 时回退 X-User-Id
 */
const baseUrl = 'http://localhost:8080'
const TOKEN_KEY = 'travel_token'

function getUserId() {
  let id = uni.getStorageSync('travel_user_id')
  if (!id) {
    id = 'guest_' + Date.now()
    uni.setStorageSync('travel_user_id', id)
  }
  return id
}

function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

function setToken(token) {
  if (token) uni.setStorageSync(TOKEN_KEY, token)
  else uni.removeStorageSync(TOKEN_KEY)
}

function clearToken() {
  uni.removeStorageSync(TOKEN_KEY)
}

function request(options) {
  const url = (options.url.indexOf('http') === 0 ? options.url : baseUrl + options.url)
  const header = {
    'Content-Type': 'application/json',
    'X-User-Id': getUserId(),
    ...(options.header || {})
  }
  const token = getToken()
  if (token && token.trim()) header['Authorization'] = 'Bearer ' + token

  console.log('发送请求:', { url, method: options.method || 'GET', data: options.data, header })
  
  return new Promise((resolve, reject) => {
    uni.request({
      url,
      method: options.method || 'GET',
      data: options.data,
      header,
      success(res) {
        console.log('请求响应:', res)
        const data = res.data || {}
        const code = res.statusCode
        if (code === 401 || data.code === 401) {
          clearToken()
          uni.showToast({ title: '请先登录', icon: 'none' })
          reject(new Error('请先登录'))
          return
        }
        if (data.code === 200 || data.code === undefined) {
          resolve(data.data !== undefined ? data.data : data)
        } else {
          reject(new Error(data.msg || '请求失败'))
        }
      },
      fail(err) {
        console.error('请求失败:', err)
        reject(err)
      }
    })
  })
}

export default {
  getUserId,
  getToken,
  setToken,
  clearToken,
  get(url, data) {
    if (data && Object.keys(data).length) {
      const q = Object.keys(data).map(k => k + '=' + encodeURIComponent(data[k])).join('&')
      url += (url.indexOf('?') >= 0 ? '&' : '?') + q
    }
    return request({ url, method: 'GET' })
  },
  post(url, data) {
    return request({ url, method: 'POST', data })
  },
  put(url, data) {
    return request({ url, method: 'PUT', data })
  },
  delete(url) {
    return request({ url, method: 'DELETE' })
  }
}
