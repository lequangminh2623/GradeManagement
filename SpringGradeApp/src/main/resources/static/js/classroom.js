/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

new TomSelect('#lecturerSelect', {
    placeholder: 'Tìm giảng viên...',
    allowEmptyOption: false
});

new TomSelect('#courseSelect', {
    placeholder: 'Tìm môn học...',
    allowEmptyOption: false
});

new TomSelect('#semesterSelect', {
    placeholder: 'Tìm học kỳ...',
    allowEmptyOption: false
});

// Chọn nhiều (Sinh viên)
new TomSelect('#studentSelect', {
    plugins: ['remove_button'],
    placeholder: 'Tìm và chọn sinh viên...'
});

function deleteClassroom(baseUrl, classroomId) {
    if (confirm("Bạn có chắc chắn muốn xóa lớp học này không?")) {
        fetch(baseUrl + classroomId, {
            method: "DELETE"
        }).then(res => {
            if (res.status === 204) {
                location.reload();
            } else {
                alert("Xóa lớp học thất bại!");
            }
        });
    }
}

function removeStudentFromClassroom(endpoint, classroomId, studentId) {
    fetch(endpoint + `${classroomId}/students/${studentId}`, {
        method: "DELETE"
    }).then(res => {
        if (res.status === 204) {
            location.reload();
        } else {
            alert("Có lỗi xảy ra");
        }
    });
}

//document.getElementById("studentSearch").addEventListener("input", function () {
//    const keyword = this.value.toLowerCase();
//    const rows = document.querySelectorAll("#studentTable tr");
//
//    rows.forEach(row => {
//        const code = row.cells[1].innerText.toLowerCase();
//        const name = row.cells[2].innerText.toLowerCase();
//        row.style.display = (code.includes(keyword) || name.includes(keyword)) ? "" : "none";
//    });
//});