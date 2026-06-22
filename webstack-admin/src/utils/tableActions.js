import { h } from 'vue'
import { NButton, NPopconfirm, NTooltip, NSpace } from 'naive-ui'

export function renderEditAction(row, onEdit) {
  return h(
    NTooltip,
    { trigger: 'hover' },
    {
      trigger: () =>
        h(
          NButton,
          {
            size: 'small',
            quaternary: true,
            circle: true,
            onClick: () => onEdit(row)
          },
          { default: () => '✎' }
        ),
      default: () => '编辑'
    }
  )
}

export function renderDeleteAction(row, onDelete, confirmText = '确认删除？') {
  return h(
    NPopconfirm,
    { onPositiveClick: () => onDelete(row) },
    {
      trigger: () =>
        h(
          NTooltip,
          { trigger: 'hover' },
          {
            trigger: () =>
              h(
                NButton,
                {
                  size: 'small',
                  quaternary: true,
                  circle: true,
                  type: 'error'
                },
                { default: () => '🗑' }
              ),
            default: () => '删除'
          }
        ),
      default: () => confirmText
    }
  )
}

export function renderActions(row, { onEdit, onDelete, deleteConfirm }) {
  return h(NSpace, { size: 6, align: 'center', wrap: false }, {
    default: () => [
      onEdit ? renderEditAction(row, onEdit) : null,
      onDelete ? renderDeleteAction(row, onDelete, deleteConfirm) : null
    ].filter(Boolean)
  })
}

export function createActionColumn({ onEdit, onDelete, deleteConfirm, width = 110 }) {
  return {
    title: '操作',
    key: 'actions',
    width,
    minWidth: width,
    render(row) {
      return renderActions(row, { onEdit, onDelete, deleteConfirm })
    }
  }
}
