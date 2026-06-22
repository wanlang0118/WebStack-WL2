const IMAGE_BASE_URL = '/uploads/'
const DEFAULT_AVATAR = ''

export function getImageUrl(fileKey) {
  if (!fileKey) {
    return DEFAULT_AVATAR
  }
  if (fileKey.startsWith('http://') || fileKey.startsWith('https://')) {
    return fileKey
  }
  return IMAGE_BASE_URL + fileKey
}
