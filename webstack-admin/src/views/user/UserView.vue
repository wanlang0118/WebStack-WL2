<template>
  <div>
    <n-space vertical :size="16">
      <n-space>
        <n-input
          v-model:value="searchKeyword"
          placeholder="搜索用户名或昵称"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
        />
        <n-button type="primary" @click="handleSearch">搜索</n-button>
        <n-button @click="handleAdd">新增用户</n-button>
      </n-space>

      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="false"
        :bordered="false"
        striped
        :scroll-x="970"
      />

      <n-pagination
        v-model:page="page"
        v-model:page-size="pageSize"
        :item-count="total"
        :page-sizes="[10, 20, 50]"
        show-size-picker
        @update:page="fetchData"
        @update:page-size="handleSizeChange"
      />
    </n-space>

    <n-modal
      v-model:show="showModal"
      :title="modalTitle"
      preset="card"
      style="width: 480px"
      :mask-closable="false"
    >
      <n-form
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-placement="left"
        label-width="80"
      >
        <n-form-item label="用户名" path="username" v-if="!isEdit">
          <n-input v-model:value="modalForm.username" placeholder="请输入用户名" />
        </n-form-item>
        <n-form-item :label="isEdit ? '密码' : '密码'" path="password">
          <n-input
            v-model:value="modalForm.password"
            type="password"
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码（至少8位，含大小写字母和数字）'"
            show-password-on="mousedown"
          />
        </n-form-item>
        <n-form-item label="头像" path="avatar">
          <upload-image v-model:value="modalForm.avatar" />
        </n-form-item>
        <n-form-item label="昵称" path="name">
          <n-input v-model:value="modalForm.name" placeholder="请输入昵称" />
        </n-form-item>
        <n-form-item label="性别" path="sex">
          <n-select v-model:value="modalForm.sex" :options="sexOptions" placeholder="请选择性别" />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input v-model:value="modalForm.email" placeholder="请输入邮箱" />
        </n-form-item>
        <n-form-item label="手机号" path="phone">
          <n-input v-model:value="modalForm.phone" placeholder="请输入手机号" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="saveLoading" @click="handleSave">保存</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import {
  NSpace,
  NInput,
  NButton,
  NDataTable,
  NPagination,
  NModal,
  NForm,
  NFormItem,
  NSelect,
  NSwitch,
  NPopconfirm,
  NImage
} from 'naive-ui'
import UploadImage from '../../components/UploadImage.vue'
import { getImageUrl } from '../../utils/image'
import { getUserList, createUser, updateUser, deleteUser, updateUserStatus } from '../../api/user'

const loading = ref(false)
const searchKeyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

const showModal = ref(false)
const modalTitle = ref('')
const isEdit = ref(false)
const saveLoading = ref(false)
const modalFormRef = ref(null)
const modalForm = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  sex: 1,
  email: '',
  phone: ''
})

const sexOptions = [
  { label: '未知', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 }
]

const modalRules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: {
    validator(rule, value) {
      if (!isEdit.value && !value) {
        return new Error('请输入密码')
      }
      if (value && value.length < 8) {
        return new Error('密码至少8位，且包含大小写字母和数字')
      }
      if (value && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(value)) {
        return new Error('密码至少8位，且包含大小写字母和数字')
      }
      return true
    },
    trigger: 'blur'
  },
  name: { required: true, message: '请输入昵称', trigger: 'blur' }
}

const columns = [
  { title: 'ID', key: 'id', width: 50 },
  { title: '用户名', key: 'username', minWidth: 100 },
  { title: '头像', key: 'avatar', width: 60, render(row) {
    return h(NImage, {
      src: getImageUrl(row.avatar),
      width: 40,
      height: 40,
      style: 'border-radius: 4px;',
      objectFit: 'cover',
      previewDisabled: true,
      fallbackSrc: ''
    })
  }},
  { title: '昵称', key: 'name', minWidth: 100 },
  { title: '性别', key: 'sex', width: 60, render(row) {
    const map = { 0: '未知', 1: '男', 2: '女' }
    return map[row.sex] || '未知'
  }},
  { title: '邮箱', key: 'email', minWidth: 140 },
  { title: '手机号', key: 'phone', minWidth: 100 },
  { title: '状态', key: 'status', width: 80, render(row) {
    return h(NSwitch, {
      value: row.status === 1,
      'on-update:value': (val) => handleStatusChange(row, val)
    })
  }},
  { title: '注册时间', key: 'createTime', minWidth: 130 },
  { title: '操作', key: 'actions', minWidth: 140, render(row) {
    return h(NSpace, { size: 8, align: 'center', wrap: false }, {
      default: () => [
        h(NButton, { size: 'small', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
        h(NPopconfirm, {
          onPositiveClick: () => handleDelete(row.id)
        }, {
          trigger: () => h(NButton, { size: 'small', type: 'error' }, { default: () => '删除' }),
          default: () => '确定删除该用户吗？'
        })
      ]
    })
  }}
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      keyword: searchKeyword.value,
      page: page.value,
      size: pageSize.value
    })
    tableData.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchData()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  page.value = 1
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增用户'
  modalForm.id = null
  modalForm.username = ''
  modalForm.password = ''
  modalForm.name = ''
  modalForm.sex = 1
  modalForm.email = ''
  modalForm.phone = ''
  modalForm.avatar = ''
  showModal.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  modalTitle.value = '编辑用户'
  modalForm.id = row.id
  modalForm.username = row.username
  modalForm.password = ''
  modalForm.name = row.name
  modalForm.sex = row.sex ?? 1
  modalForm.email = row.email || ''
  modalForm.phone = row.phone || ''
  modalForm.avatar = row.avatar || ''
  showModal.value = true
}

const handleSave = () => {
  modalFormRef.value?.validate(async (errors) => {
    if (errors) return
    saveLoading.value = true
    try {
      const data = {
        name: modalForm.name,
        sex: modalForm.sex,
        email: modalForm.email,
        phone: modalForm.phone,
        avatar: modalForm.avatar
      }
      if (modalForm.password) {
        data.password = modalForm.password
      }
      if (isEdit.value) {
        data.id = modalForm.id
        await updateUser(modalForm.id, data)
      } else {
        data.username = modalForm.username
        data.password = modalForm.password
        await createUser(data)
      }
      showModal.value = false
      fetchData()
    } finally {
      saveLoading.value = false
    }
  })
}

const handleStatusChange = async (row, val) => {
  try {
    await updateUserStatus(row.id, val ? 1 : 0)
    row.status = val ? 1 : 0
  } catch (e) {
    row.status = val ? 0 : 1
  }
}

const handleDelete = async (id) => {
  await deleteUser(id)
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>
