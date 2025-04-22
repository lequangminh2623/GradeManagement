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

new TomSelect('#studentSelect', {
    plugins: ['remove_button'],
    placeholder: 'Tìm và chọn sinh viên...'
});

function deleteClassroom(url, id) {
    if (confirm("Bạn chắc chắn muốn xóa?")) {
        fetch(`${url}${id}`, {
            method: 'DELETE'
        }).then(res => {
            if (res.status === 204) {
                alert("Xóa thành công!");
                location.reload();
            } else {
                return res.text().then(text => {
                    alert(text);
                });
            }
        });
    }
}

function removeStudentFromClassroom(endpoint, classroomId, studentId) {
    if (confirm("Bạn có chắc chắn muốn xóa sinh viên và điểm khỏi lớp học này không?")) {
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
}

let maxExtra = 3;

function addExtraColumn() {
    const headerRow = document.querySelector("#extra-header-buttons");
    const anyCell = document.querySelector(".extra-grade-cells");

    if (!anyCell)
        return;

    const existingIndices = Array.from(anyCell.children)
            .map(input => parseInt(input.name.match(/\[(\d+)\]$/)?.[1]))
            .filter(n => !isNaN(n));

    let newIndex = 0;
    while (existingIndices.includes(newIndex)) {
        newIndex++;
    }

    if (newIndex >= maxExtra)
        return;

    document.querySelectorAll(".extra-grade-cells").forEach((cell) => {
        const studentId = cell.getAttribute("data-student-id");

        const input = document.createElement("input");
        input.type = "number";
        input.className = "form-control form-control-sm text-center";
        input.name = `extraPoints[${studentId}][${newIndex}]`;
        input.min = 0;
        input.max = 10;
        input.step = 0.1;
        input.style.width = "60px";

        cell.appendChild(input);
    });

    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.className = "btn btn-outline-danger btn-sm";
    removeButton.innerText = "❌";
    removeButton.setAttribute("onclick", `removeExtraColumn(${newIndex})`);

    headerRow.appendChild(removeButton);
}

function removeExtraColumn(index) {
    if (confirm(`Bạn có chắc muốn xoá cột điểm bổ sung ${index + 1}?`)) {
        document.querySelectorAll(".extra-grade-cells").forEach(cell => {
            const inputs = cell.querySelectorAll("input");
            if (inputs.length > index) {
                inputs[index].remove();
            }
        });

        const headerButtons = document.querySelector("#extra-header-buttons");
        if (headerButtons && headerButtons.children.length > index) {
            headerButtons.children[index].remove();
        }
    }
}
