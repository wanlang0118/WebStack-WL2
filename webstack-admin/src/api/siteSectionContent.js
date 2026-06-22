import request from './request.js'

export const getSiteSectionContentList = (sectionId) => request.get('/site-section-content/list', { params: { sectionId } })
export const getSiteSectionContentById = (id) => request.get(`/site-section-content/${id}`)
export const saveSiteSectionContent = (data) => request.post('/site-section-content/save', data)
export const updateSiteSectionContent = (data) => request.put('/site-section-content/update', data)
export const deleteSiteSectionContent = (id) => request.delete(`/site-section-content/${id}`)
export const deleteSiteSectionContentBatch = (ids) => request.post('/site-section-content/batch-delete', ids)
