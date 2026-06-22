import request from './request.js'

export const getTagPage = (params) => request.get('/tag/page', { params })
export const getTagById = (id) => request.get(`/tag/${id}`)
export const getTagList = () => request.get('/tag/list')
export const saveTag = (data) => request.post('/tag/save', data)
export const updateTag = (data) => request.put('/tag/update', data)
export const deleteTag = (id) => request.delete(`/tag/${id}`)
export const deleteTagBatch = (ids) => request.post('/tag/batch-delete', ids)
