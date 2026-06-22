import request from './request.js'

export const getLikePage = (params) => request.get('/site/like/page', { params })
export const deleteLikeRecord = (id) => request.delete(`/site/like/record/${id}`)
export const deleteLikeBatch = (ids) => request.post('/site/like/batch-delete', ids)
