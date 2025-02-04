CREATE VIEW form_view AS
SELECT DISTINCT ON (f.id) f.id, fr.created_at AS changed_at, fr.created_by AS changed_by, fp.created_at AS published_at, fp.created_by AS published_by, f.path, f.skjemanummer, fr.revision, fr.title, fr.properties
	FROM form f
	JOIN form_revision fr ON f.id = fr.form_id
	LEFT JOIN form_publication fp ON f.id = fp.form_id
	ORDER BY f.id, fr.revision DESC, fp.created_at DESC;
