import request from './request'

export function getUserList(params) {
  return request.get('/user', { params })
}

export function getUserDetail(id) {
  return request.get(`/user/${id}`)
}

export function createUser(data) {
  return request.post('/user', data)
}

export function updateUser(id, data) {
  return request.put(`/user/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

export function updateUserStatus(id, status) {
  return request.put(`/user/${id}/status`, null, { params: { status } })
}
