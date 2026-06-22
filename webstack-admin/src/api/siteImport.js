import request from './request.js'

export const importSites = (data) => request.post('/site/import', data, { timeout: 120000 })

export const aiParseSites = (data) => request.post('/site/import/ai-parse', data, { timeout: 120000 })
