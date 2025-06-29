BASE_URL = "http://localhost:8080";

async function getUsers() {
    const response = await fetch(`${BASE_URL}/users`);

    if (response.status !== 200) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function getUser(userId) {
    const response = await fetch(`${BASE_URL}/users/${userId}`);

    if (response.status !== 200) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function createUser(name, surname, email, cf) {
    const response = await fetch(`${BASE_URL}/users`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, surname, email, cf }),
    });

    if (response.status !== 201) {
        throw new Error(await response.text(), { cause: response.status });
    }

    return await response.json();
}

async function getVouchers() {
    const response = await fetch(`${BASE_URL}/vouchers`);

    if (response.status !== 200) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function getVouchersForUser(userId) {
    const response = await fetch(`${BASE_URL}/vouchers?userId=${userId}`);

    if (response.status !== 200) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function getVoucher(voucherId) {
    const response = await fetch(`${BASE_URL}/vouchers/${voucherId}`);

    if (response.status !== 200) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function useVoucher(voucherId) {
    const response = await fetch(`${BASE_URL}/vouchers/${voucherId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ used: true }),
    });

    if (response.status !== 200) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function createVoucher(userId, value, type) {
    const response = await fetch(`${BASE_URL}/vouchers`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userId, value, type }),
    });

    if (response.status !== 201) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function updateVoucher(voucherId, type) {
    const response = await fetch(`${BASE_URL}/vouchers/${voucherId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ type }),
    });

    if (response.status !== 200) {
        throw new Error(await response.text());
    }

    return await response.json();
}

async function deleteVoucher(voucherId) {
    const response = await fetch(`${BASE_URL}/vouchers/${voucherId}`, {
        method: 'DELETE',
    });


    if (response.status !== 204) {
        throw new Error(await response.text());
    }
}
