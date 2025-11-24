<template>
  <div class="article-comment clearfix" :style="{ marginLeft: depth * 20 + 'px' }">
    <figure class="article-comment-user-image">
      <img 
        src="https://www.gravatar.com/avatar/631577fc0428c1dbc6176a3ca5935f77?s=110&d=identicon" 
        alt="binh-luan" 
        class="block" 
      />
    </figure>
    <div class="article-comment-user-comment">
              <p class="user-name-comment">
          <strong>{{ comment.userFullName }}</strong>
          <span v-if="comment.parentCommentId" class="text-muted ms-2">
            <i class="bi bi-reply"></i> Phản hồi
          </span>
          <span v-if="depth > 0" class="badge bg-secondary ms-2">Reply</span>
        </p>
        <!-- Hiển thị tên người được phản hồi -->
        <div v-if="comment.parentCommentId && getParentCommentName(comment.parentCommentId)" class="reply-to-info mb-2">
          <small class="text-primary">
            <i class="bi bi-arrow-return-right"></i> 
            Phản hồi <strong>{{ getParentCommentName(comment.parentCommentId) }}</strong>
            <span class="ms-2 text-muted">• {{ formatDate(comment.createdDate) }}</span>
          </small>
        </div>
              <span class="article-comment-date-bull">{{ formatDate(comment.createdDate) }}</span>
        <p class="cm">{{ getCleanContent(comment.content) }}</p>
      
      <!-- Nút reply -->
      <div class="comment-actions mt-2">
        <button 
          v-if="canReply && isLoggedIn" 
          @click="handleReply" 
          class="btn btn-sm btn-outline-primary"
          type="button"
        >
          <i class="bi bi-reply"></i> Phản hồi
        </button>
        <button 
          v-else-if="canReply && !isLoggedIn" 
          @click="handleReply" 
          class="btn btn-sm btn-outline-secondary"
          type="button"
          title="Đăng nhập để phản hồi"
        >
          <i class="bi bi-reply"></i> Phản hồi
        </button>
      </div>
      
      <!-- Hiển thị replies -->
      <div v-if="comment.replies && comment.replies.length > 0" class="comment-replies mt-3">
        <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
          <CommentItem 
            :comment="reply" 
            :comments="comments"
            @reply="(comment) => $emit('reply', comment)"
            :depth="depth + 1"
            :isLoggedIn="isLoggedIn"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  comments: {
    type: Array,
    required: true
  },
  depth: {
    type: Number,
    default: 0
  },
  isLoggedIn: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['reply']);

const maxDepth = 3; // Giới hạn độ sâu bình luận lồng

const canReply = computed(() => props.depth < maxDepth);

const handleReply = () => {
  if (canReply.value) {
    emit('reply', props.comment);
  }
};

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    return new Date(dateString).toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    console.error('Lỗi định dạng ngày:', e);
    return 'N/A';
  }
};

// Hàm lấy tên người được phản hồi
const getParentCommentName = (parentCommentId) => {
  const parentComment = props.comments.find(comment => comment.commentId === parentCommentId);
  return parentComment ? parentComment.userFullName : null;
};

// Hàm làm sạch nội dung bình luận (loại bỏ @tên_người ở đầu)
const getCleanContent = (content) => {
  if (!content) return '';
  
  // Loại bỏ @tên_người ở đầu nội dung nếu có
  const cleanContent = content.replace(/^@\w+\s*/, '');
  return cleanContent.trim();
};
</script>

<style scoped>
.article-comment {
  border-left: 3px solid transparent;
  transition: all 0.3s ease;
  margin-bottom: 15px;
}

.article-comment:hover {
  border-left-color: #007bff;
  background-color: #f8f9fa;
}

.comment-replies {
  border-left: 2px solid #e9ecef;
  padding-left: 15px;
  margin-top: 10px;
  position: relative;
}

.comment-replies::before {
  content: '';
  position: absolute;
  left: -2px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, #007bff, #e9ecef);
}

.reply-item {
  margin-bottom: 10px;
}

.comment-actions {
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.article-comment:hover .comment-actions {
  opacity: 1;
}

.comment-actions .btn {
  font-size: 0.875rem;
  padding: 0.25rem 0.5rem;
}

.reply-to-info {
  background-color: #f8f9fa;
  border-left: 3px solid #007bff;
  padding: 8px 12px;
  border-radius: 4px;
  margin-bottom: 8px;
}

.reply-to-info small {
  font-size: 0.875rem;
}

.reply-to-info i {
  margin-right: 4px;
}

/* Responsive cho bình luận */
@media (max-width: 768px) {
  .article-comment {
    margin-left: 10px !important;
  }
  
  .comment-replies {
    padding-left: 10px;
  }
  
  .comment-actions .btn {
    font-size: 0.8rem;
    padding: 0.2rem 0.4rem;
  }
}
</style> 