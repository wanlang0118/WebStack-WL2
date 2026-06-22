import request from './request'

export const getTagList = () => request.get('/tag/list')
