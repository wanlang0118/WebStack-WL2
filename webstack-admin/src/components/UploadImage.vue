<template>
  <div class="upload-image-wrapper">
    <n-upload
      v-if="!value"
      :show-file-list="false"
      accept="image/*"
      :custom-request="handleCustomRequest"
      @before-upload="handleBeforeUpload"
    >
      <n-upload-dragger class="upload-dragger">
        <div class="upload-placeholder">
          <span class="upload-icon">+</span>
          <span>点击上传</span>
        </div>
      </n-upload-dragger>
    </n-upload>
    <div v-else class="image-preview">
      <n-image :src="previewUrl" object-fit="cover" class="preview-img" preview-disabled />
      <n-button class="remove-btn" size="tiny" type="error" @click="handleRemove">
        删除
      </n-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { NUpload, NUploadDragger, NImage, NButton } from 'naive-ui'
import { uploadImage } from '../api/upload'
import { getImageUrl } from '../utils/image'

const props = defineProps({
  value: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:value'])

const previewUrl = computed(() => getImageUrl(props.value))

function handleBeforeUpload({ file }) {
  const maxSize = 5 * 1024 * 1024
  if (file.file.size > maxSize) {
    window.$message?.error('文件大小不能超过 5MB')
    return false
  }
  return true
}

async function handleCustomRequest({ file, onFinish, onError }) {
  try {
    const key = await uploadImage(file.file)
    emit('update:value', key)
    onFinish()
    window.$message?.success('上传成功')
  } catch (e) {
    onError()
    window.$message?.error(e.message || '上传失败')
  }
}

function handleRemove() {
  emit('update:value', '')
}
</script>

<style scoped>
.upload-image-wrapper {
  width: 120px;
}
.upload-dragger {
  width: 120px;
  height: 120px;
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 12px;
  gap: 4px;
}
.upload-icon {
  font-size: 28px;
  font-weight: 300;
  line-height: 1;
}
.image-preview {
  position: relative;
  width: 120px;
  height: 120px;
}
.preview-img {
  width: 120px;
  height: 120px;
  border-radius: 4px;
}
.remove-btn {
  position: absolute;
  bottom: 4px;
  right: 4px;
  opacity: 0.85;
}
</style>
