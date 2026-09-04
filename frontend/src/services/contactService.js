import axiosClient from '../api/axiosClient';

const contactService = {
  getContacts: (page = 0, size = 10, search = '') => {
    const params = {
      page,
      size,
    };
    if (search) {
      params.search = search;
    }
    return axiosClient.get('/contacts', { params });
  },

  getContactById: (id) => {
    return axiosClient.get(`/contacts/${id}`);
  },

  createContact: (contactData) => {
    return axiosClient.post('/contacts', contactData);
  },

  updateContact: (id, contactData) => {
    return axiosClient.put(`/contacts/${id}`, contactData);
  },

  deleteContact: (id) => {
    return axiosClient.delete(`/contacts/${id}`);
  },
};

export default contactService;
