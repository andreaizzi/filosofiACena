BASE_URL = "http://localhost:8080";

async function getUsers() {
    const response = await fetch(`${BASE_URL}/users`);

    if (response.status === 404) {
        throw new Error("Users not found");
    } else if (!response.ok) {
        throw new Error(`Error fetching users: ${response.statusText}`);
    }

    return await response.json();
}

async function getUser(userId) {
    const response = await fetch(`${BASE_URL}/users/${userId}`);

    if (response.status === 404) {
        throw new Error("User not found");
    } else if (!response.ok) {
        throw new Error(`Error fetching user: ${response.statusText}`);
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

    if (response.status === 409) {
        throw new Error("User already exists");
    } else if (response.status === 400) {
        throw new Error("Invalid user data");
    } else if (!response.ok) {
        throw new Error(`Error creating user: ${response.statusText}`);
    }

    return await response.json();
}

async function getVouchers() {
    const response = await fetch(`${BASE_URL}/vouchers`);

    if (response.status === 404) {
        throw new Error("Vouchers not found");
    } else if (!response.ok) {
        throw new Error(`Error fetching vouchers: ${response.statusText}`);
    }

    return await response.json();
}

async function getVouchersForUser(userId) {
    const response = await fetch(`${BASE_URL}/vouchers?userId=${userId}`);

    if (response.status === 404) {
        throw new Error("Vouchers for user not found");
    } else if (!response.ok) {
        throw new Error(`Error fetching vouchers for user: ${response.statusText}`);
    }

    return await response.json();
}

async function getVoucher(voucherId) {
    const response = await fetch(`${BASE_URL}/vouchers/${voucherId}`);

    if (response.status === 404) {
        throw new Error("Voucher not found");
    } else if (!response.ok) {
        throw new Error(`Error fetching voucher: ${response.statusText}`);
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

    if (response.status === 404) {
        throw new Error("Voucher not found");
    } else if (!response.ok) {
        throw new Error(`Error using voucher: ${response.statusText}`);
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

    if (response.status === 409) {
        throw new Error("Voucher already exists");
    } else if (response.status === 400) {
        throw new Error("Invalid voucher data");
    } else if (!response.ok) {
        throw new Error(`Error creating voucher: ${response.statusText}`);
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

    if (response.status === 404) {
        throw new Error("Voucher not found");
    } else if (response.status === 400) {
        throw new Error("Invalid voucher data");
    } else if (!response.ok) {
        throw new Error(`Error updating voucher: ${response.statusText}`);
    }

    return await response.json();
}

async function deleteVoucher(voucherId) {
    const response = await fetch(`${BASE_URL}/vouchers/${voucherId}`, {
        method: 'DELETE',
    });


    if (response.status === 404) {
        throw new Error("Voucher not found");
    } else if (!response.ok) {
        throw new Error(`Error deleting voucher: ${response.statusText}`);
    }
}
