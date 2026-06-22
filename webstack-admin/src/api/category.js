import request from './request.js'

export const getCategoryTree = () => request.get('/category/tree')
export const saveCategory = (data) => request.post('/category/save', data)
export const updateCategory = (data) => request.put('/category/update', data)
export const deleteCategory = (id) => request.delete(`/category/${id}`)
export const deleteCategoryBatch = (ids) => request.post('/category/batch-delete', ids)
