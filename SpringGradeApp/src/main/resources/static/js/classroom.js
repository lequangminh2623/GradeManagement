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

let maxExtra = 3;

function addExtraColumn() {
    const headerRow = document.querySelector("#extra-header-buttons");
    const anyCell = document.querySelector(".extra-grade-cells");

    if (!anyCell) return;

    // Lấy danh sách chỉ số hiện tại từ một học sinh đại diện
    const existingIndices = Array.from(anyCell.children)
        .map(input => parseInt(input.name.match(/\[(\d+)\]$/)?.[1]))
        .filter(n => !isNaN(n));

    let newIndex = 0;
    while (existingIndices.includes(newIndex)) {
        newIndex++;
    }

    if (newIndex >= maxExtra) return;

    // Thêm input mới cho từng học sinh
    document.querySelectorAll(".extra-grade-cells").forEach((cell) => {
        const studentId = cell.getAttribute("data-student-id");

        const input = document.createElement("input");
        input.type = "number";
        input.className = "form-control form-control-sm text-center";
        input.name = `extraPoints[${studentId}][${newIndex}]`;
        input.placeholder = `BS ${newIndex + 1}`;
        input.min = 0;
        input.max = 10;
        input.step = 0.1;
        input.style.width = "60px";

        cell.appendChild(input);
    });

    // Thêm nút ❌ vào header một lần duy nhất cho mỗi index
    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.className = "btn btn-outline-danger btn-sm";
    removeButton.innerText = "❌";
    removeButton.setAttribute("onclick", `removeExtraColumn(${newIndex})`);

    headerRow.appendChild(removeButton);
}

function confirmRemoveExtraColumn(index) {
    if (confirm(`Bạn có chắc muốn xoá cột điểm bổ sung ${index + 1}?`)) {
        removeExtraColumn(index);
    }
}

function removeExtraColumn(index) {
    // Xóa input điểm bổ sung tại vị trí index cho mỗi sinh viên
    document.querySelectorAll(".extra-grade-cells").forEach(cell => {
        const inputs = cell.querySelectorAll("input");
        if (inputs.length > index) {
            inputs[index].remove();
        }
    });

    // Xóa nút ❌ tương ứng ở phần header
    const headerButtons = document.querySelector("#extra-header-buttons");
    if (headerButtons && headerButtons.children.length > index) {
        headerButtons.children[index].remove();
    }
}
