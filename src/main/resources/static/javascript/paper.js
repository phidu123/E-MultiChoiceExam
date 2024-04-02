document.addEventListener("DOMContentLoaded", function() {
    setupShowScoreLogic();

    function setupShowScoreLogic() {
        var timesAllowedInput = document.getElementById("time-allowed");
        var showScoreYesRadio = document.getElementById("show-score-yes");
        var flashElement = document.querySelector(".flash");

        showScoreYesRadio.addEventListener("click", function() {
            if (parseInt(timesAllowedInput.value) > 1) {
                flashElement.classList.add("animate--drop-in-fade-out");
                setTimeout(function() {
                    flashElement.classList.remove("animate--drop-in-fade-out");
                }, 4000);
                this.checked = false;
                document.getElementById("show-score-no").checked = true;
            }
        });
    }
});

let selectedFileNames = [];

function openFileInput() {
	document.getElementById('fileInput').click();
}

function handleFileChange(event) {
    const fileList = event.target.files;

    // Clear the existing files
    selectedFileNames = [];

    // Add the new file to the array
    if (fileList.length > 0) {
		selectedFileNames.push({
			name: fileList[0].name,
			size: fileList[0].size
		});
	}

    // Update the displayed file list
    updateSelectedFiles();
}

function formatFileSize(size) {
	const units = ["B", "KB", "MB", "GB"];
	let index = 0;

	while (size >= 1024 && index < units.length - 1) {
		size /= 1024;
		index++;
	}

	return `${size.toFixed(2)} ${units[index]}`;
}

function removeFile(index) {
	selectedFileNames.splice(index, 1);
	updateSelectedFiles();
}

function updateSelectedFiles() {
	const selectedFilesList = document.getElementById('selectedFiles');
	const fileItems = selectedFileNames.map((f, index) => `
		<li class="item">
			<span class="name">
				${f.name} (${formatFileSize(f.size)})
			</span>
			<div class="remove" onclick="removeFile(${index})">
				<i class="fa fa-trash-o"
					style="
                        font-size: 20px;
                        font-weight: bold;
                    "></i>
			</div>
		</li>
	`).join('');

	selectedFilesList.innerHTML = `<ul class="file-list">${fileItems}</ul>`;
}