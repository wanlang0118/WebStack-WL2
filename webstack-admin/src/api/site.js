import request from './request.js'

export const getSitePage = (params) => request.get('/site/page', { params })
export const getSiteById = (id) => request.get(`/site/${id}`)
export const saveSite = (data) => request.post('/site/save', data)
export const updateSite = (data) => request.put('/site/update', data)
export const deleteSite = (id) => request.delete(`/site/${id}`)
export const deleteSiteBatch = (ids) => request.post('/site/batch-delete', ids)
