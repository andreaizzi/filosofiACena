function createSplittedBar(parts, fillerColor = null) {
    if (!Array.isArray(parts) || parts.length === 0) {
        throw new Error('Parts must be a non-empty array');
    }

    const totalWidth = parts.reduce((sum, part) => sum + part.width, 0);
    if (totalWidth > 1) {
        throw new Error('Total width of parts must be at most 1');
    }

    const barContainer = document.createElement('div');
    barContainer.style.width = '100%';
    barContainer.style.height = '20px';
    barContainer.style.display = 'flex';
    barContainer.style.borderRadius = '8px';
    barContainer.style.overflow = 'hidden';

    parts.forEach(part => {
        const { width, color } = part;
        const partElement = document.createElement('div');
        partElement.style.width = `${width * 100}%`;
        partElement.style.height = '20px';
        partElement.style.backgroundColor = color;
        partElement.style.textAlign = 'center';
        barContainer.appendChild(partElement);
    });

    // Aggiunta del filler solo se necessario
    if (totalWidth < 1) {
        const fillerElement = document.createElement('div');
        fillerElement.style.width = `${(1 - totalWidth) * 100}%`;
        fillerElement.style.height = '20px';
        fillerElement.style.backgroundColor = fillerColor || 'transparent';
        barContainer.appendChild(fillerElement);
    }

    // Legenda
    const legendContainer = document.createElement('div');
    legendContainer.style.display = 'flex';
    legendContainer.style.gap = '16px';
    legendContainer.style.marginTop = '8px';
    legendContainer.style.alignItems = 'center';

    parts.forEach(part => {
        if (!part.caption) return;
        const legendItem = document.createElement('div');
        legendItem.style.display = 'flex';
        legendItem.style.alignItems = 'center';
        legendItem.style.gap = '6px';

        const colorBox = document.createElement('span');
        colorBox.style.display = 'inline-block';
        colorBox.style.width = '16px';
        colorBox.style.height = '16px';
        colorBox.style.backgroundColor = part.color;
        colorBox.style.borderRadius = '4px';
        colorBox.style.border = '1px solid #444';

        const caption = document.createElement('span');
        caption.textContent = part.caption;

        legendItem.appendChild(colorBox);
        legendItem.appendChild(caption);
        legendContainer.appendChild(legendItem);
    });

    // Wrapper per barra + legenda
    const wrapper = document.createElement('div');
    wrapper.appendChild(barContainer);
    wrapper.appendChild(legendContainer);

    return wrapper;
}

function createVouchersTable(vouchers) {
    if (!Array.isArray(vouchers) || vouchers.length === 0) {
        const message = document.createElement('p');
        message.textContent = 'Non ci sono buoni.';
        return message;
    }

    const table = document.createElement('table');
    const headerRow = document.createElement('tr');
    const headers = ['Valore', 'Tipo', 'Data di Creazione', 'Utilizzato'];
    headers.forEach(headerText => {
        const th = document.createElement('th');
        th.textContent = headerText;
        headerRow.appendChild(th);
    });

    table.appendChild(headerRow);
    vouchers.forEach(voucher => {
        const row = document.createElement('tr');
        const cells = [
            `${voucher.value.toFixed(2)} €`,
            voucher.type,
            new Date(voucher.creationDate.replace('[UTC]', '')).toLocaleDateString(),
            voucher.used ? 'Sì' : 'No'
        ];

        cells.forEach(cellText => {
            const td = document.createElement('td');
            td.textContent = cellText;
            row.appendChild(td);
        });

        table.appendChild(row);
    });

    return table;
}

function createNotConsumedVouchersTable(vouchers) {
    if (!Array.isArray(vouchers) || vouchers.length === 0) {
        const message = document.createElement('p');
        message.textContent = 'Non ci sono buoni non utilizzati.';
        return message;
    }

    const table = document.createElement('table');

    const headerRow = document.createElement('tr');
    const headers = ['Valore', 'Tipo', 'Data di Creazione', ''];
    headers.forEach(headerText => {
        const th = document.createElement('th');
        th.textContent = headerText;
        headerRow.appendChild(th);
    });
    table.appendChild(headerRow);

    vouchers.forEach(voucher => {
        const row = document.createElement('tr');
        const cells = [
            `${voucher.value.toFixed(2)} €`,
            voucher.type,
            new Date(voucher.creationDate.replace('[UTC]', '')).toLocaleDateString(),
        ];

        cells.forEach(cellText => {
            const td = document.createElement('td');
            td.textContent = cellText;
            row.appendChild(td);
        });

        const actionCell = document.createElement('td');
        const consumeButton = document.createElement('button');
        consumeButton.textContent = 'Usa';
        consumeButton.onclick = (event) => {
            event.preventDefault();

            if (confirm('Sei sicuro di voler utilizzare questo buono?')) {
                useVoucher(voucher.id)
                    .then(() => {
                        window.location.reload();
                    })
                    .catch(error => {
                        console.error('Error consuming voucher:', error);
                        alert(error.message || 'Impossibile utilizzare il buono. Riprova più tardi.');
                    });
            }
        };
        actionCell.appendChild(consumeButton);

        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Elimina';
        deleteButton.onclick = (event) => {
            event.preventDefault();

            if (confirm('Sei sicuro di voler eliminare questo buono?')) {
                deleteVoucher(voucher.id)
                    .then(() => {
                        window.location.reload();
                    })
                    .catch(error => {
                        console.error('Error deleting voucher:', error);
                        alert(error.message || 'Impossibile eliminare il buono. Riprova più tardi.');
                    });
            };
        };
        actionCell.appendChild(deleteButton);

        const editButton = document.createElement('button');
        editButton.textContent = 'Modifica';
        editButton.className = 'edit-button';
        editButton.onclick = (event) => {
            event.preventDefault();
            window.location.href = `../dashboard/edit-voucher/?id=${voucher.id}`;
        };
        actionCell.appendChild(editButton);

        row.appendChild(actionCell);

        table.appendChild(row);
    });

    return table;
}

function createConsumedVouchersTable(vouchers) {
    if (!Array.isArray(vouchers) || vouchers.length === 0) {
        const message = document.createElement('p');
        message.textContent = 'Non ci sono buoni utilizzati.';
        return message;
    }

    const table = document.createElement('table');

    const headerRow = document.createElement('tr');
    const headers = ['Valore', 'Tipo', 'Data di Creazione', 'Data di Utilizzo'];
    headers.forEach(headerText => {
        const th = document.createElement('th');
        th.textContent = headerText;
        headerRow.appendChild(th);
    });
    table.appendChild(headerRow);

    vouchers.forEach(voucher => {
        const row = document.createElement('tr');
        const cells = [
            `${voucher.value.toFixed(2)} €`,
            voucher.type,
            new Date(voucher.creationDate.replace('[UTC]', '')).toLocaleDateString(),
            new Date(voucher.consumeDate.replace('[UTC]', '')).toLocaleDateString()
        ];

        cells.forEach(cellText => {
            const td = document.createElement('td');
            td.textContent = cellText;
            row.appendChild(td);
        });

        table.appendChild(row);
    });

    return table;
}