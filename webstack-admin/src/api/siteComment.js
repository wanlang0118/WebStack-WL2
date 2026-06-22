import request from './request.js'

export const getCommentPage = (params) => request.get('/site/comment/page', { params })
export const updateCommentStatus = (id, status) => request.put(`/site/comment/status/${id}`, null, { params: { status } })
export const replyComment = (data) => request.post('/site/comment/reply', data)
export const updateCommentContent = (id, content) => request.put(`/site/comment/content/${id}`, null, { params: { content } })
export const deleteComment = (id) => request.delete(`/site/comment/record/${id}`)
export const deleteCommentBatch = (ids) => request.post('/site/comment/batch-delete', ids)
