const IMAGE_BASE_URL = '/uploads/'

export function getImageUrl(fileKey) {
  if (!fileKey) return ''
  if (fileKey.startsWith('http://') || fileKey.startsWith('https://')) {
    return fileKey
  }
  if (fileKey.startsWith('/')) {
    return fileKey
  }
  return IMAGE_BASE_URL + fileKey
}
