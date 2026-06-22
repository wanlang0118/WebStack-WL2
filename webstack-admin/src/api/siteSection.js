import request from './request.js'

export const getSiteSectionList = (siteId) => request.get('/site-section/list', { params: { siteId } })
export const getSiteSectionById = (id) => request.get(`/site-section/${id}`)
export const saveSiteSection = (data) => request.post('/site-section/save', data)
export const updateSiteSection = (data) => request.put('/site-section/update', data)
export const deleteSiteSection = (id) => request.delete(`/site-section/${id}`)
export const deleteSiteSectionBatch = (ids) => request.post('/site-section/batch-delete', ids)
