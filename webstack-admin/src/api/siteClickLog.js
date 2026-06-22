import request from './request.js'

export const getClickLogPage = (params) => request.get('/site/click/page', { params })
