import request from './request.js'

export const getNoticePage = (params) => request.get('/notice/page', { params })
export const broadcastNotice = (data) => request.post('/notice/broadcast', data)
export const markNoticeRead = (id) => request.put(`/notice/read/${id}`)
export const deleteNotice = (id) => request.delete(`/notice/${id}`)
export const deleteNoticeBatch = (ids) => request.post('/notice/batch-delete', ids)
