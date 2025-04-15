new TomSelect('#userSelect', {
    placeholder: 'Tìm người đăng...',
    allowEmptyOption: false
});

new TomSelect('#classroomSelect', {
    placeholder: 'Tìm lớp học...',
    allowEmptyOption: false
});

function deleteForumPost(url, id) {
    if (confirm("Bạn chắc chắn xóa không?")) {
        fetch(`${url}${id}`, {
            method: "delete"
        }).then(res => {
            if (res.status === 204) {
                alert("Xóa thành công!");
                location.reload();
            } else
                alert("Có lỗi xảy ra!");
        });
    }
}

