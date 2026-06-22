import request from './request'

export const getCommentPage = (params) => request.get('/site/comment/page', { params })

export const submitComment = (data) => request.post('/site/comment/submit', data)
